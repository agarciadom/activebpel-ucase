// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/storage/AeProcessImplStateAttributeDefaults.java,v 1.25 2007/04/23 23:28:2
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
package org.activebpel.rt.bpel.impl.storage;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.activebpel.rt.bpel.IAeBusinessProcess;
import org.activebpel.rt.bpel.impl.AeBpelState;
import org.activebpel.rt.bpel.impl.IAeBusinessProcessEngineInternal;
import org.activebpel.rt.bpel.impl.IAeImplStateNames;
import org.activebpel.rt.bpel.impl.IAeProcessManager;
import org.activebpel.rt.bpel.impl.reply.IAeReplyReceiver;
import org.activebpel.rt.bpel.impl.reply.IAeTransmissionTracker;

/**
 * Maps persistence attribute names to default values for those attributes.
 */
public class AeProcessImplStateAttributeDefaults implements IAeImplStateNames
{
   /** Singleton instance. */
   private static final AeProcessImplStateAttributeDefaults sDefaults =
      new AeProcessImplStateAttributeDefaults();

   /** The underlying unmodifiable <code>Map</code>. */
   private final Map mDefaults;

   /**
    * Private constructor for singleton instance.
    */
   private AeProcessImplStateAttributeDefaults()
   {
      Map defaults = new HashMap();

      defaults.put(STATE_DATA                 , "no"); //$NON-NLS-1$
      defaults.put(STATE_ENABLED              , "true"); //$NON-NLS-1$
      defaults.put(STATE_ENDDATE              , ""); //$NON-NLS-1$
      defaults.put(STATE_ENDDATEMILLIS        , ""); //$NON-NLS-1$
      defaults.put(STATE_EVAL                 , "true"); //$NON-NLS-1$
      defaults.put(STATE_FIRST_ITER           , "true"); //$NON-NLS-1$
      defaults.put(STATE_FOREACH_COMPLETIONCONDITION, "-1"); //$NON-NLS-1$
      defaults.put(STATE_FOREACH_COMPLETIONCOUNT, "-1"); //$NON-NLS-1$
      defaults.put(STATE_FOREACH_COUNTER      , "-1"); //$NON-NLS-1$
      defaults.put(STATE_INSTANCE_VALUE       , "1"); //$NON-NLS-1$
      defaults.put(STATE_INSTANCE_COUNT       , "0"); //$NON-NLS-1$
      defaults.put(STATE_FOREACH_FINAL        , "-1"); //$NON-NLS-1$
      defaults.put(STATE_FOREACH_START        , "-1"); //$NON-NLS-1$
      defaults.put(STATE_HASATTACHMENTS       , "false"); //$NON-NLS-1$
      defaults.put(STATE_HASDATA              , "true"); //$NON-NLS-1$
      defaults.put(STATE_HASIMPLICITCOMPENSATIONHANDLER, "false"); //$NON-NLS-1$  
      defaults.put(STATE_HASIMPLICITTERMINATIONHANDLER, "false"); //$NON-NLS-1$  
      defaults.put(STATE_HASIMPLICITFAULTHANDLER, "false"); //$NON-NLS-1$
      defaults.put(STATE_LOOP_TERMINATION_REASON            , "0");  //$NON-NLS-1$
      defaults.put(STATE_INIT                 , "true"); //$NON-NLS-1$
      defaults.put(STATE_MAXLOCATIONID        , "-1"); //$NON-NLS-1$
      defaults.put(STATE_NEXTVARIABLEID       , "-1"); //$NON-NLS-1$
      defaults.put(STATE_NEXTINDEX            , "0"); //$NON-NLS-1$
      defaults.put(STATE_NORMALCOMPLETION     , "true"); //$NON-NLS-1$
      defaults.put(STATE_PROCESSSTATE         , "" + IAeBusinessProcess.PROCESS_RUNNING); //$NON-NLS-1$
      defaults.put(STATE_PROCESSSTATEREASON   , "0"); //$NON-NLS-1$
      defaults.put(STATE_QUEUED               , "false"); //$NON-NLS-1$
      defaults.put(STATE_SCOPE_LOCATION       , ""); //$NON-NLS-1$
      defaults.put(STATE_SKIPCHILDREN         , "false"); //$NON-NLS-1$
      defaults.put(STATE_STATE                , AeBpelState.INACTIVE.toString());
      defaults.put(STATE_TERMINATING          , "false"); //$NON-NLS-1$
      defaults.put(STATE_VERSION              , "0"); //$NON-NLS-1$
      defaults.put(STATE_SNAPSHOTRECORDED     , "false"); //$NON-NLS-1$
      defaults.put(STATE_HASCOORDINATIONS     , "false"); //$NON-NLS-1$
      defaults.put(STATE_HASCOORDCOMPENSATOR  , "false"); //$NON-NLS-1$
      defaults.put(STATE_COORDINATION_ID      , ""); //$NON-NLS-1$
      defaults.put(STATE_CALLBACK_COORDINATED , "false"); //$NON-NLS-1$
      defaults.put(STATE_CALLBACK_COORD_ID    , ""); //$NON-NLS-1$
      defaults.put(STATE_COORDINATION_COUNT   , "0"); //$NON-NLS-1$
      defaults.put(STATE_COORDINATOR         , "false"); //$NON-NLS-1$
      defaults.put(STATE_PARTICIPANT         , "false"); //$NON-NLS-1$
      defaults.put(STATE_TRANSMISSION_ID      , String.valueOf(IAeTransmissionTracker.NULL_TRANSREC_ID));
      defaults.put(STATE_REPLY_ID             , String.valueOf(IAeReplyReceiver.NULL_REPLY_ID) ); 
      defaults.put(STATE_ENGINE_ID            , String.valueOf(IAeBusinessProcessEngineInternal.NULL_ENGINE_ID) );
      defaults.put(STATE_DOC_VERSION          , IAeImplStateNames.STATE_DOC_1_0); 
      defaults.put(STATE_INVOKE_ID            , "0"); //$NON-NLS-1$
      defaults.put(STATE_ALARM_ID             , "0"); //$NON-NLS-1$
      defaults.put(STATE_JOURNAL_ID           , String.valueOf(IAeProcessManager.NULL_JOURNAL_ID));
      mDefaults = Collections.unmodifiableMap(defaults);
   }

   /**
    * Returns singleton instance.
    */
   public static AeProcessImplStateAttributeDefaults getDefaults()
   {
      return sDefaults;
   }

   /**
    * Returns the default value for the specified attribute name.
    */
   public String get(String key)
   {
      return (String) mDefaults.get(key);
   }
}
