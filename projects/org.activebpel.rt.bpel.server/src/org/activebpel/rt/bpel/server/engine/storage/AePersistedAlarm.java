//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/storage/AePersistedAlarm.java,v 1.2 2006/09/18 17:59:5
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
package org.activebpel.rt.bpel.server.engine.storage;

import java.util.Date;


/**
 * A persisted alarm.
 */
public class AePersistedAlarm implements IAePersistedAlarm
{
   /** The alarm's process id. */
   private long mProcessId;
   /** The alarm's location path id. */
   private int mLocationPathId;
   /** The alarm's deadline. */
   private Date mDeadline;
   /** The alarm's group id. */
   private int mGroupId;
   /** Alarm execution instance id. */
   private int mAlarmId;

   /**
    * Simple contructor.
    * 
    * @param aProcessId
    * @param aLocationPathId
    * @param aDeadline
    * @param aGroupId
    * @param aAlarmId
    */
   public AePersistedAlarm(long aProcessId, int aLocationPathId, Date aDeadline, int aGroupId, int aAlarmId)
   {
      setProcessId(aProcessId);
      setLocationPathId(aLocationPathId);
      setDeadline(aDeadline);
      setGroupId(aGroupId);
      setAlarmId(aAlarmId);
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.IAePersistedAlarm#getProcessId()
    */
   public long getProcessId()
   {
      return mProcessId;
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.IAePersistedAlarm#getLocationPathId()
    */
   public int getLocationPathId()
   {
      return mLocationPathId;
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.IAePersistedAlarm#getDeadline()
    */
   public Date getDeadline()
   {
      return mDeadline;
   }
   
   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.IAePersistedAlarm#getGroupId()
    */
   public int getGroupId()
   {
      return mGroupId;
   }
   
   /**
    * @param aGroupId The groupLocationId to set.
    */
   protected void setGroupId(int aGroupId)
   {
      mGroupId = aGroupId;
   }
   
   /**
    * @param aDeadline The deadline to set.
    */
   protected void setDeadline(Date aDeadline)
   {
      mDeadline = aDeadline;
   }
   
   /**
    * @param aLocationPathId The locationPathId to set.
    */
   protected void setLocationPathId(int aLocationPathId)
   {
      mLocationPathId = aLocationPathId;
   }
   
   /**
    * @param aProcessId The processId to set.
    */
   protected void setProcessId(long aProcessId)
   {
      mProcessId = aProcessId;
   }

   /**
    * @return Returns the alarmId.
    */
   public int getAlarmId()
   {
      return mAlarmId;
   }

   /**
    * @param aAlarmId The alarmId to set.
    */
   protected void setAlarmId(int aAlarmId)
   {
      mAlarmId = aAlarmId;
   }
   
   
}
