// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/io/readers/AeBPWSBpelReader.java,v 1.2 2006/10/12 20:15:2
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
package org.activebpel.rt.bpel.def.io.readers;

import org.activebpel.rt.bpel.def.io.registry.IAeBpelRegistry;

/**
 * Reader impl for BPEL4WS 1.1
 */
public class AeBPWSBpelReader extends AeRegistryBasedBpelReader
{
   /**
    * Ctor accepts the registries.
    * 
    * @param aBpelRegistry
    * @param aExtRegistry
    */
   public AeBPWSBpelReader(IAeBpelRegistry aBpelRegistry)
   {
      super(aBpelRegistry);
   }
}
