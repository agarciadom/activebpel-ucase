// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/activity/AeActivityAssignImpl.java,v 1.60 2006/07/17 21:58:4
/*
 * Copyright (c) 2004-2006 Active Endpoints, Inc.
 *
 * This program is licensed under the terms of the GNU General Public License
 * Version 2 (the "License") as published by the Free Software Foundation, and 
 * the ActiveBPEL Licensing Policies (the "Policies").  A copy of the License 
 * and the Policies were distributed with this program.  
 *
 * The License is available at:
 * http: *www.gnu.org/copyleft/gpl.html
 *
 * The Policies are available at:
 * http: *www.activebpel.org/licensing/index.html
 *
 * Unless required by applicable law or agreed to in writing, this program is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
 * OF ANY KIND, either express or implied.  See the License and the Policies
 * for specific language governing the use of this program.
 */
package org.activebpel.rt.bpel.impl.activity; 

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.IAeProcessInfoEvent;
import org.activebpel.rt.bpel.def.activity.AeActivityAssignDef;
import org.activebpel.rt.bpel.impl.AeBpelException;
import org.activebpel.rt.bpel.impl.AeFaultFactory;
import org.activebpel.rt.bpel.impl.AeProcessInfoEvent;
import org.activebpel.rt.bpel.impl.IAeActivityParent;
import org.activebpel.rt.bpel.impl.activity.assign.AeAtomicCopyOperationContext;
import org.activebpel.rt.bpel.impl.activity.assign.AeCopyOperationBase;
import org.activebpel.rt.bpel.impl.activity.assign.AeCopyOperationContext;
import org.activebpel.rt.bpel.impl.activity.assign.IAeAssignOperation;
import org.activebpel.rt.bpel.impl.activity.assign.IAeCopyOperationContext;
import org.activebpel.rt.bpel.impl.activity.assign.IAeTo;
import org.activebpel.rt.bpel.impl.activity.assign.to.AeToBase;
import org.activebpel.rt.bpel.impl.activity.assign.to.AeToVariableMessagePart;
import org.activebpel.rt.bpel.impl.visitors.IAeImplVisitor;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Implementation of the BPEL assign activity.
 */
public class AeActivityAssignImpl extends AeActivityImpl
{
    private static final String XML_NAMESPACE = "http://www.w3.org/2000/xmlns/";

   /** list of copy operations to get executed */
   private List mCopyOperations = new LinkedList();
   
   /** Copy operation context used by assign activity */
   private IAeCopyOperationContext mCopyOperationContext;
   
   /**
    * Ctor accepts the def and parent
    * 
    * @param aAssign
    * @param aParent
    */
   public AeActivityAssignImpl(AeActivityAssignDef aAssign, IAeActivityParent aParent)
   {
      super(aAssign, aParent);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.AeAbstractBpelObject#accept(org.activebpel.rt.bpel.impl.visitors.IAeImplVisitor)
    */
   public void accept(IAeImplVisitor aVisitor) throws AeBusinessProcessException
   {
      aVisitor.visit(this);
   }
   
   /**
    * Returns a copy operation context for the assign activity.
    */
   public IAeCopyOperationContext getCopyOperationContext()
   {
      if (mCopyOperationContext == null)
         mCopyOperationContext = new AeAtomicCopyOperationContext(this);
      
      return mCopyOperationContext;
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeExecutableQueueItem#execute()
    */
   public void execute() throws AeBusinessProcessException
   {
      boolean success = false;
      AeAtomicCopyOperationContext copyContext = (AeAtomicCopyOperationContext)getCopyOperationContext();
      
      try
      {
         executeOperations();
         copyContext.clearRollback();
         success = true;
      }
      catch(Throwable t)
      {
         // Restore data to the initial state and signal a fault
         copyContext.rollback();
         if (t instanceof AeBpelException)
         {
            objectCompletedWithFault(((AeBpelException)t).getFault());
         }
         else
         {
            objectCompletedWithFault(AeFaultFactory.getSystemErrorFault(t));
         }
      }
      
      if (success)
         objectCompleted();
   }

   /**
    * Executes all of the copy operations as well as any extensible operations
    * in the order that they appeared in the def. If there are any errors during
    * the execution then we'll throw and the assign will rollback any modified 
    * variables.
    * 
    * @throws AeBusinessProcessException
    */
   protected void executeOperations() throws AeBusinessProcessException
   {
      int index = 0;
      try
      {
         for (Iterator iter = getCopyOperations().iterator(); iter.hasNext(); index++)
         {
            IAeAssignOperation operation = (IAeAssignOperation) iter.next();

            // Log copy start
            AeProcessInfoEvent evtI = new AeProcessInfoEvent(getProcess().getProcessId(),
                    String.format("%s/copy[%d]", getLocationPath(), index),
                    IAeProcessInfoEvent.GENERIC_INFO_EVENT,
                    "", " : Executing");
            getProcess().getEngine().fireInfoEvent(evtI);

            operation.execute();

            // Print copy destination (if possible)
            String path = "unknown";
            if (operation instanceof AeCopyOperationBase) {
                AeCopyOperationBase copyOp = (AeCopyOperationBase)operation;
                IAeTo              iToElem = copyOp.getTo();
                if (iToElem instanceof AeToBase) {
                    AeToBase toElem = (AeToBase)iToElem;

                    // We need the variable name (and part, if any)
                    path = "$" + toElem.getVariableName();
                    if (toElem instanceof AeToVariableMessagePart) {
                        path = path + "." + ((AeToVariableMessagePart)toElem).getPart();
                    }

                    // Print the path to the element if the result is an XML node
                    Object destino = toElem.getTarget();
                    if (destino instanceof Node) {
                        path = path + computePath((Node)destino);
                    }
                }
            }

            // ... and log copy completion
            AeProcessInfoEvent evtF = new AeProcessInfoEvent(getProcess().getProcessId(),
                    String.format("%s/copy[%d]", getLocationPath(), index),
                    IAeProcessInfoEvent.GENERIC_INFO_EVENT,
                    "", String.format(" : Completed normally assignment to '%s'", path));
            getProcess().getEngine().fireInfoEvent(evtF);
         }
      }
      catch(Throwable t)
      {
         // Log info error message to give user clue as to which operation failed.
         // Note we are sending the index of the copy operation which is translated in the msg formatter
         AeProcessInfoEvent evt = new AeProcessInfoEvent(getProcess().getProcessId(),
                                                 getLocationPath(),
                                                 IAeProcessInfoEvent.ERROR_ASSIGN_ACTIVITY,
                                                 "", //$NON-NLS-1$
                                                 Integer.toString(index));
         getProcess().getEngine().fireInfoEvent(evt);
         
         if (t instanceof AeBusinessProcessException)
            throw (AeBusinessProcessException)t;
         else
            throw new AeBusinessProcessException(t.getMessage(), t);
      }
   }
   
   /**
    * Adds the copy operation to our list
    * 
    * @param aCopyOperation
    */
   public void addCopyOperation(IAeAssignOperation aCopyOperation)
   {
      getCopyOperations().add(aCopyOperation);
   }

   /**
    * @return Returns the copyOperations.
    */
   protected List getCopyOperations()
   {
      return mCopyOperations;
   }

   /**
    * @param aCopyOperations The copyOperations to set.
    */
   protected void setCopyOperations(List aCopyOperations)
   {
      mCopyOperations = aCopyOperations;
   }

   /**
    * Returns the absolute path of the XML node from the root of its tree.
    * Copied over from the XPath extension function uca:imprimirRutas.
    * */
   private String computePath(Node node) {
        final String sName = resolveNameWithBPELPrefixes(getCopyOperationContext(), node);

        if (node instanceof Document || node instanceof DocumentFragment) {
            return "";
        } else if (node instanceof Attr) {
            final Node nCont = ((Attr) node).getOwnerElement();
            return String.format("%s/@%s", computePath(nCont), sName);
        } else {
            final Node nParent = node.getParentNode();
            int position = 1;

            // Obtenemos posicion entre los hermanos con el mismo nombre
            final NodeList nlChildren = nParent.getChildNodes();
            for (int i = 0; i < nlChildren.getLength(); ++i) {
                final Node nChildren = nlChildren.item(i);
                if (nChildren.getNodeName().equals(sName)) {
                    if (nChildren == node) {
                        break;
                    } else {
                        position++;
                    }
                }
            }

            return String.format("%s/%s[%d]", computePath(nParent), sName,
                    position);
        }
    }

    private static String resolveNameWithBPELPrefixes(
            IAeCopyOperationContext context, Node node) {
        // We need to resolve the prefix using the BPEL process' context,
        // or the prefixes won't match correctly (we would end up using
        // those from the BPELUnit test suite specification instead of
        // the WS-BPEL process definition's)
        final String nbLocalAtr = node.getLocalName();
        final String nsAtr = node.getNamespaceURI();
        String nbAtr = nbLocalAtr;
        if (nsAtr != null) {
            if (XML_NAMESPACE.equals(nsAtr)) {
                nbAtr = "xmlns:" + nbLocalAtr;
            } else {
                final String nsPrefix = (String) context
                        .resolveNamespaceToPrefixes(nsAtr).toArray()[0];
                nbAtr = String.format("%s:%s", nsPrefix, nbLocalAtr);
            }
        }
        return nbAtr;
    }
}
