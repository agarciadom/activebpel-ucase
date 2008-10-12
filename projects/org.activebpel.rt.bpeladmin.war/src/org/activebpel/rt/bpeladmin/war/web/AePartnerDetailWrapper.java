// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpeladmin.war/src/org/activebpel/rt/bpeladmin/war/web/AePartnerDetailWrapper.java,v 1.3 2005/01/26 22:23:2
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
package org.activebpel.rt.bpeladmin.war.web;

import javax.xml.namespace.QName;

import org.activebpel.rt.bpel.server.deploy.IAeDeploymentContext;
import org.activebpel.rt.util.AeUtil;

/**
 * Wraps the details of a pdef row.
 */
public class AePartnerDetailWrapper
{
   /** PartnerLinkType QName */
   protected QName mPartnerLinkType;
   /** Role. */
   protected String mRole;
   /** Endpoint service QName. */
   protected QName mEndpoint;
   /** Comma delimited string a deployment short names. */
   protected String mDeployments;
   
   /**
    * Constructor.
    * @param aPartnerLinkType PLT QName
    * @param aRole The pdef role.
    * @param aEndpoint The endpoint service QName.
    * @param aDeployments Array of associated deployment contexts.
    */
   public AePartnerDetailWrapper(QName aPartnerLinkType, String aRole, 
                     QName aEndpoint, IAeDeploymentContext[] aDeployments )
   {
      mPartnerLinkType = aPartnerLinkType;
      mRole = aRole;
      mEndpoint = aEndpoint;
      mDeployments = extract( aDeployments );
   }
   
   /**
    * Utility methods for formatting QNames.
    * @param aQName
    */
   protected String format( QName aQName )
   {
      StringBuffer sb = new StringBuffer();
      if( !AeUtil.isNullOrEmpty(aQName.getNamespaceURI()) )
      {
         sb.append( aQName.getNamespaceURI() );
         sb.append( ":" ); //$NON-NLS-1$
      }
      sb.append( aQName.getLocalPart() );
      return sb.toString();
   }
   
   /**
    * Utility method to extract deployment short names from
    * context array.
    * @param aContexts
    */
   protected String extract( IAeDeploymentContext[] aContexts )
   {
      StringBuffer sb = new StringBuffer();
      String sep = ""; //$NON-NLS-1$
      for( int i = 0; i < aContexts.length; i++ )
      {
         sb.append( sep );
         sb.append( aContexts[i].getShortName() );
         sep=","; //$NON-NLS-1$
      }
      return sb.toString();
   }
   
   /**
    * Getter for deployments string.
    */
   public String getDeployments()
   {
      return mDeployments;
   }

   /**
    * Getter for endpoint service qname string.
    */
   public String getEndpoint()
   {
      return format( mEndpoint );
   }
   
   /**
    * Getter for endpoint service qname local part. 
    */
   public String getEndpointLocalPart()
   {
      return mEndpoint.getLocalPart();
   }
   
   /**
    * Getter for partner link type qname string.
    */
   public String getPartnerLinkType()
   {
      return format( mPartnerLinkType );
   }
   
   /**
    * Getter for partner link type qname local part. 
    */
   public String getPartnerLinkLocalPart()
   {
      return mPartnerLinkType.getLocalPart();
   }

   /**
    * Getter for the partner link type qname namespace.
    */
   public String getPartnerLinkNamespace()
   {
      return mPartnerLinkType.getNamespaceURI();
   }

   /**
    * Getter for role.
    */
   public String getRole()
   {
      return mRole;
   }
}
