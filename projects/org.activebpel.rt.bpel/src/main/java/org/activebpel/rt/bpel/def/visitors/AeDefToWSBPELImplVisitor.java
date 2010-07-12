//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/visitors/AeDefToWSBPELImplVisitor.java,v 1.19 2007/09/12 02:48:1
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
package org.activebpel.rt.bpel.def.visitors; 

import org.activebpel.rt.bpel.IAeActivity;
import org.activebpel.rt.bpel.def.AeCatchDef;
import org.activebpel.rt.bpel.def.AeCorrelationSetDef;
import org.activebpel.rt.bpel.def.AeCorrelationsDef;
import org.activebpel.rt.bpel.def.AeVariableDef;
import org.activebpel.rt.bpel.def.IAeBPELConstants;
import org.activebpel.rt.bpel.def.activity.AeActivityAssignDef;
import org.activebpel.rt.bpel.def.activity.AeActivityScopeDef;
import org.activebpel.rt.bpel.def.activity.AeActivityValidateDef;
import org.activebpel.rt.bpel.def.activity.AeNotUnderstoodExtensionActivityDef;
import org.activebpel.rt.bpel.def.activity.support.AeOnAlarmDef;
import org.activebpel.rt.bpel.def.activity.support.AeOnEventDef;
import org.activebpel.rt.bpel.def.faults.AeFaultMatchingStrategyFactory;
import org.activebpel.rt.bpel.impl.AeBPELMessageDataValidator;
import org.activebpel.rt.bpel.impl.AeVariablesImpl;
import org.activebpel.rt.bpel.impl.IAeBpelObject;
import org.activebpel.rt.bpel.impl.IAeBusinessProcessEngineInternal;
import org.activebpel.rt.bpel.impl.IAeBusinessProcessInternal;
import org.activebpel.rt.bpel.impl.IAeMessageValidator;
import org.activebpel.rt.bpel.impl.IAeProcessPlan;
import org.activebpel.rt.bpel.impl.activity.AeActivityAssignWSBPELImpl;
import org.activebpel.rt.bpel.impl.activity.AeActivityEmptyImpl;
import org.activebpel.rt.bpel.impl.activity.AeActivityOnEventScopeImpl;
import org.activebpel.rt.bpel.impl.activity.AeActivityScopeImpl;
import org.activebpel.rt.bpel.impl.activity.AeActivityValidateImpl;
import org.activebpel.rt.bpel.impl.activity.IAeScopeTerminationStrategy;
import org.activebpel.rt.bpel.impl.activity.IAeWSIOActivity;
import org.activebpel.rt.bpel.impl.activity.assign.AeVirtualCopyOperation;
import org.activebpel.rt.bpel.impl.activity.assign.from.AeFromStrategyFactory;
import org.activebpel.rt.bpel.impl.activity.assign.to.AeToVariableElement;
import org.activebpel.rt.bpel.impl.activity.assign.to.AeToVariableMessage;
import org.activebpel.rt.bpel.impl.activity.assign.to.AeToVariableType;
import org.activebpel.rt.bpel.impl.activity.support.AeCorrelationSet;
import org.activebpel.rt.bpel.impl.activity.support.AeIMACorrelations;
import org.activebpel.rt.bpel.impl.activity.support.AeOnEvent;
import org.activebpel.rt.bpel.impl.activity.support.AeOnEventCorrelations;
import org.activebpel.rt.bpel.impl.activity.support.AeRepeatableOnAlarm;
import org.activebpel.rt.bpel.impl.activity.support.AeWSBPELCorrelationSet;
import org.activebpel.rt.bpel.impl.activity.support.AeWSBPELFaultHandler;
import org.activebpel.rt.bpel.impl.activity.support.AeIMACorrelations.IAeCorrelationSetFilter;
import org.activebpel.rt.bpel.impl.activity.wsio.consume.AeOnMessageConsumerContext;

/**
 * Creates impl objects for WS-BPEL 2.0 
 */
public class AeDefToWSBPELImplVisitor extends AeDefToImplVisitor
{
   /** strategy for terminating a 2.0 scope */
   private static final IAeScopeTerminationStrategy WSBPEL_ScopeTerminationStrategy = new AeWSBPELScopeTerminationStrategy();
   
   /** message validator */
   private static final IAeMessageValidator MESSAGE_VALIDATOR = new AeBPELMessageDataValidator(false);

   /**
    * Constructor - inits the visitor
    * @param aPid - id of the process visitor will create
    * @param aEngine - engine that will own the process
    * @param aPlan - deployment plan for the process
    */
   public AeDefToWSBPELImplVisitor(long aPid,
         IAeBusinessProcessEngineInternal aEngine, IAeProcessPlan aPlan)
   {
      super(aPid, aEngine, aPlan);
      init();
   }

   /**
    * Constructor - inits the visitor
    * @param aProcess - process that will own any activities created
    * @param aParent - parent for the initial activity creation
    */
   public AeDefToWSBPELImplVisitor(IAeBusinessProcessInternal aProcess,
         IAeBpelObject aParent)
   {
      super(aProcess, aParent);
      init();
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.visitors.AeDefToImplVisitor#createTraverser()
    */
   protected IAeDefVisitor createTraverser()
   {
      return new AeTraversalVisitor(new AeWSBPELImplementationTraverser(), this);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.AeDefToImplVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityAssignDef)
    */
   public void visit(AeActivityAssignDef aDef)
   {
      AeActivityAssignWSBPELImpl impl = new AeActivityAssignWSBPELImpl(aDef, getActivityParent());
      getActivityParent().addActivity(impl);
      traverse(aDef, impl);
   }

   /**
    * Creates the scope implementation and then traverses it.
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityScopeDef)
    */
   public void visit(AeActivityScopeDef aDef)
   {
      // TODO (MF) provide a separate def for the onEvent scope?
      AeActivityScopeImpl impl = getActivityParent() instanceof AeOnEvent ? new AeActivityOnEventScopeImpl(aDef, (AeOnEvent)getActivityParent()) : new AeActivityScopeImpl(aDef, getActivityParent());
      impl.setTerminationStrategy(getScopeTerminationStrategy());
      impl.setFaultMatchingStrategy(getFaultMatchingStrategy());
      getActivityParent().addActivity(impl);
      traverse(aDef, impl);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.AeDefToImplVisitor#visit(org.activebpel.rt.bpel.def.AeCatchDef)
    */
   public void visit(AeCatchDef aDef)
   {
      AeWSBPELFaultHandler fh = new AeWSBPELFaultHandler(aDef, getScope());
      getScope().addFaultHandler(fh);
      traverse(aDef, fh);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.AeDefToImplVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeOnEventDef)
    */
   public void visit(AeOnEventDef aDef)
   {
      AeOnEvent onEvent = new AeOnEvent(aDef, getMessageParent());
      getMessageParent().addMessage(onEvent);
      onEvent.setMessageValidator(getMessageValidator());
      
      assignMessageDataConsumer(onEvent, new AeOnMessageConsumerContext(onEvent), aDef);

      traverse(aDef, onEvent);
      
      // move the child scope that was created for the onEvent from its list of 
      // children to the queuing scope. This scope is only used to provide 
      // instance info the plink and such when the onEvent queues. The normal
      // behavior for the onEvent is for it to defer the creation of its child 
      // scope instances until the message for the onEvent arrives.
      AeActivityOnEventScopeImpl queuingScope = (AeActivityOnEventScopeImpl) onEvent.getChildren().get(0);
      onEvent.setQueuingScope(queuingScope);
      if (onEvent.isConcurrent())
      {
         onEvent.getChildren().clear();
      }
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.AeDefToImplVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeOnAlarmDef)
    */
   public void visit(AeOnAlarmDef aDef)
   {
      if (aDef.getRepeatEveryDef() == null)
      {
         super.visit(aDef);
      }
      else
      {
         AeRepeatableOnAlarm alarm = new AeRepeatableOnAlarm(aDef, getMessageParent());
         getMessageParent().addAlarm(alarm);
         traverse(aDef, alarm);
      }
   }

   /**
    * Setup the visitor to install the correct strategies for termination and fault matching 
    */
   protected void init()
   {
      setScopeTerminationStrategy(WSBPEL_ScopeTerminationStrategy);
      setFaultMatchingStrategy(AeFaultMatchingStrategyFactory.getInstance(IAeBPELConstants.WSBPEL_2_0_NAMESPACE_URI));
      setMessageValidator(MESSAGE_VALIDATOR);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.AeDefToImplVisitor#visit(org.activebpel.rt.bpel.def.AeVariableDef)
    */
   public void visit(AeVariableDef aVarDef)
   {
      super.visit(aVarDef);
      if (aVarDef.getFromDef() != null)
      {
         AeVirtualCopyOperation copyOp = AeVirtualCopyOperation.createVariableInitializer();
         copyOp.setFrom(AeFromStrategyFactory.createFromStrategy(aVarDef.getFromDef())); 
         if (aVarDef.isMessageType())
            copyOp.setTo(new AeToVariableMessage(aVarDef.getName()));
         else if (aVarDef.isElement())
            copyOp.setTo(new AeToVariableElement(aVarDef.getName()));
         else if (aVarDef.isType())
            copyOp.setTo(new AeToVariableType(aVarDef.getName()));
         AeVariablesImpl variablesImpl = (AeVariablesImpl) getVariableContainer();
         variablesImpl.addCopyOperation(copyOp);
      }         
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityValidateDef)
    */
   public void visit(AeActivityValidateDef aDef)
   {
      IAeActivity impl = new AeActivityValidateImpl(aDef, getActivityParent());
      getActivityParent().addActivity(impl);
      traverse(aDef, impl);
   }

   /**
    * Create the correlation set implementation object and add it to the scope.
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeCorrelationSetDef)
    */
   public void visit(AeCorrelationSetDef aDef)
   {
      AeCorrelationSet set = new AeWSBPELCorrelationSet(aDef, getScope());
      getScope().addCorrelationSet(set);
      traverse(aDef, set);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.AeDefToImplVisitor#visit(AeNotUnderstoodExtensionActivityDef)
    */
   public void visit(AeNotUnderstoodExtensionActivityDef aDef)
   {
      // Convert all unknown extension activities to Empty impls (note that 
      // validation would have prevented us from getting here if the extension 
      // were mustUnderstand == 'yes').
      IAeActivity impl = new AeActivityEmptyImpl(aDef, getActivityParent());
      getActivityParent().addActivity(impl);
      traverse(aDef, impl);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.AeDefToImplVisitor#visit(org.activebpel.rt.bpel.def.AeCorrelationsDef)
    */
   public void visit(AeCorrelationsDef aDef)
   {
      IAeWSIOActivity parent = (IAeWSIOActivity) peek();
      
      if (parent instanceof AeOnEvent)
      {
         AeOnEventCorrelations correlations = new AeOnEventCorrelations(aDef, (AeOnEvent) parent);
         correlations.setFilter(getCorrelationsFilter());
         parent.setRequestCorrelations(correlations);
         traverse(aDef, null);
      }
      else
      {
         super.visit(aDef);
      }
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.AeDefToImplVisitor#getCorrelationsFilter()
    */
   protected IAeCorrelationSetFilter getCorrelationsFilter()
   {
      return AeIMACorrelations.INITIALIZED;
   }
}
