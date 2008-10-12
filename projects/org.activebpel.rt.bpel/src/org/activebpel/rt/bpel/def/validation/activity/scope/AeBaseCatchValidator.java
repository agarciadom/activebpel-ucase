//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/validation/activity/scope/AeBaseCatchValidator.java,v 1.2 2006/10/06 21:32:4
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
package org.activebpel.rt.bpel.def.validation.activity.scope; 

import java.util.Set;

import javax.xml.namespace.QName;

import org.activebpel.rt.bpel.def.AeCatchDef;
import org.activebpel.rt.bpel.def.faults.IAeCatch;
import org.activebpel.rt.bpel.def.validation.AeBaseValidator;
import org.activebpel.rt.bpel.def.validation.AeVariableValidator;
import org.activebpel.rt.bpel.def.visitors.preprocess.strategies.AeBaseSpec;
import org.activebpel.rt.util.AeUtil;

/**
 * Base class for catch validation. Provides the basic functionality
 * of validating the catch def patterns.
 */
public abstract class AeBaseCatchValidator extends AeBaseValidator implements IAeCatch
{
   /** optional variable that is caught */
   private AeVariableValidator mVariable;
   /** flag used to avoid checking for variables mutliple times */
   private boolean mUnresolvedVariable;

   /**
    * Ctor
    * @param aCatchDef
    */
   public AeBaseCatchValidator(AeCatchDef aCatchDef)
   {
      super(aCatchDef);
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.validation.AeBaseValidator#validate()
    */
   public void validate()
   {
      super.validate();

      // ensure that catch block has either a variable or a fault name defined, according to the rules in the spec
      if (!isValidPattern())
      {
         getReporter().addError( getPatternErrorMessage(), null, getDef() );
      }
      
      // this will validate the variable reference
      resolveVariable();
      
      if ( getDef().getActivityDef() == null )
         getReporter().addError( ERROR_EMPTY_FAULT_HANDLER, null, getDef() );
   }

   /**
    * @see org.activebpel.rt.bpel.def.faults.IAeCatch#getFaultName()
    */
   public QName getFaultName()
   {
      return getDef().getFaultName();
   }

   /**
    * @see org.activebpel.rt.bpel.def.faults.IAeCatch#hasFaultVariable()
    */
   public boolean hasFaultVariable()
   {
      return AeUtil.notNullOrEmpty(getDef().getFaultVariable());
   }

   /**
    * @see org.activebpel.rt.bpel.def.faults.IAeCatch#getFaultElementName()
    */
   public QName getFaultElementName()
   {
      AeVariableValidator variable = resolveVariable();
      if (variable == null)
         return null;
      else
      {
         return variable.getDef().getElement();
      }
   }

   /**
    * @see org.activebpel.rt.bpel.def.faults.IAeCatch#getFaultMessageType()
    */
   public QName getFaultMessageType()
   {
      AeVariableValidator variable = resolveVariable();
      if (variable == null)
         return null;
      else
      {
         return variable.getDef().getMessageType();
      }
   }

   /**
    * Getter for the optional variable on the catch
    */
   protected AeVariableValidator resolveVariable()
   {
      if (getVariable() == null && !isUnresolvedVariable() && hasFaultVariable())
      {
         setVariable(getVariableValidator(getDef().getFaultVariable(), AeCatchDef.TAG_FAULT_VARIABLE, true));
         setUnresolvedVariable(mVariable == null);
      }
      return getVariable();
   }
   

   /**
    * Gets the pattern error message
    */
   protected abstract String getPatternErrorMessage();

   /**
    * Returns the patterns supported for this version of bpel
    */
   protected abstract Set getPatterns();
   
   /**
    * Returns true if the catch def conforms to a valid pattern
    */
   protected boolean isValidPattern()
   {
      AeCatchSpec spec = AeCatchSpec.create(getDef());
      return getPatterns().contains(spec);
   }
   
   /**
    * Getter for the def
    */
   protected AeCatchDef getDef()
   {
      return (AeCatchDef) getDefinition();
   }

   /**
    * If true, there's no reason to search for the variable again since it would
    * result in multiple error messages
    */
   protected boolean isUnresolvedVariable()
   {
      return mUnresolvedVariable;
   }

   /**
    * Setter for the unresolved flag which is used to avoid reporting unresolved error multiple times
    * @param aFlag
    */
   protected void setUnresolvedVariable(boolean aFlag)
   {
      mUnresolvedVariable = aFlag;
   }

   /**
    * Getter for the variable
    */
   protected AeVariableValidator getVariable()
   {
      return mVariable;
   }

   /**
    * @param aVariableModel
    */
   protected void setVariable(AeVariableValidator aVariableModel)
   {
      mVariable = aVariableModel;
   }

   /**
    * Inner class for a catch specification. Used to determine if the catch has the 
    * proper combination of attributes.
    */
   protected static class AeCatchSpec extends AeBaseSpec
   {
      private static final int FAULT_NAME = 1;
      private static final int FAULT_VARIABLE = 2;
      private static final int MESSAGE_TYPE = 3;
      private static final int ELEMENT_TYPE = 4;
      
      /**
       * No arg ctor
       */
      protected AeCatchSpec()
      {
         super();
      }
      
      /**
       * Creates a spec that matches the def
       * @param aCatchDef
       */
      public static AeCatchSpec create(AeCatchDef aCatchDef)
      {
         AeCatchSpec spec = new AeCatchSpec();
         
         if (aCatchDef.getFaultName() != null)
            spec.setFaultName();
         if (AeUtil.notNullOrEmpty(aCatchDef.getFaultVariable()))
            spec.setFaultVariable();
         if (aCatchDef.getFaultElementName() != null)
            spec.setElementType();
         if (aCatchDef.getFaultMessageType() != null)
            spec.setMessageType();
         
         return spec;
      }
      
      /**
       * sets the fault name bit
       */
      public void setFaultName()
      {
         set(FAULT_NAME);
      }
      
      /**
       * sets the fault variable bit 
       */
      public void setFaultVariable()
      {
         set(FAULT_VARIABLE);
      }
      
      /**
       * sets the message type bit
       */
      public void setMessageType()
      {
         set(MESSAGE_TYPE);
      }
      
      /**
       * sets the element type bit
       */
      public void setElementType()
      {
         set(ELEMENT_TYPE);
      }
   }
}
 
