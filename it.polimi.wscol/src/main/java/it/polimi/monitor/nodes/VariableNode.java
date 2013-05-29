/*
 Copyright 2007 Politecnico di Milano
 This file is part of Dynamo.

 Dynamo is free software; you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation; either version 3 of the License, or
 (at your option) any later version.

 Dynamo is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package it.polimi.monitor.nodes;


import it.polimi.exception.WSCoLException;
import it.polimi.monitor.InputMonitor;


public class VariableNode extends NodeWSCoL {

        private static final long serialVersionUID = 345501908200925896L;
        private SimpleAST identifier;
        private XPathExpressionNode xpath=null;
        private String extraPath;
        private AliasNodeInfo aliasNodeInfo=null;
        private Alias alias=null;

        @Override
        public void evaluate(InputMonitor inputMonitor, Aliases aliases , AliasNodes tempAliases) throws WSCoLException {
                //The first child represents the identifier of the Variable
                identifier = (SimpleAST)getFirstChild();
                identifier.evaluate(inputMonitor, aliases, tempAliases);
                //controllo se id è un'alias temporaneo
                String id=identifier.getMonitoringValue();
                if (tempAliases.isKnowAliasNode(id)){
                        AliasNode aliasNode=tempAliases.getAliasNode(id);
                        String x="";
                        if(this.getNextSibling()!=null) {
                                xpath = ((XPathExpressionNode)this.getNextSibling());
                                xpath.evaluate(inputMonitor,  aliases,  tempAliases);
                                x=xpath.getMonitoringValue();
                        }
                        switch (aliasNode.getTypeOfExtraction()) {
                                case AliasNode.EXTRACTALLTOGETHER:
                                        aliasNodeInfo=new AliasNodeInfo(AliasNode.EXTRACTALLTOGETHER,aliasNode.getRootXml()+x,aliasNode.getMonitoringValue(),aliasNode.getNumberOfCurrentChildren());
                                        break;
                                case AliasNode.EXTRACTSTEPBYSTEP:
                                        aliasNodeInfo=new AliasNodeInfo(AliasNode.EXTRACTSTEPBYSTEP,aliasNode.getRootXml()+x,aliasNode.getMonitoringValue(),aliasNode.getNumberOfCurrentChildren());
                                        break;
                                default:
                                        aliasNodeInfo=new AliasNodeInfo(AliasNode.EXTRACTSTEPBYSTEP,aliasNode.getRootXml()+x,aliasNode.getMonitoringValue(),aliasNode.getNumberOfCurrentChildren());
                                        break;
                        }
                } else if(aliases.isKnowAlias(id)){
                        //			controllo se id è un'alias
                        alias=aliases.getAlias(id);
                        String x="";
                        if(this.getNextSibling()!=null && alias.getAliasType() == Alias.ALIAS_VAR) {
                                xpath = ((XPathExpressionNode)this.getNextSibling());
                                xpath.evaluate(inputMonitor,  aliases ,  tempAliases);
                                x=xpath.getMonitoringValue();
                                alias.setExtraPath(x);
                        }
                } else {//the second child represents the Xpath of the Variable. The Xpath of the variable could be null
                        if(this.getNextSibling()!=null) {
                                xpath = ((XPathExpressionNode)this.getNextSibling());
                                xpath.evaluate(inputMonitor,  aliases, tempAliases);
                        }
                }
        }

        @Override
        public Object getMonitoringValue() throws WSCoLException {
                if (aliasNodeInfo!=null) {
                //	aliasNodeInfo
                        return aliasNodeInfo;
                } else if (alias!=null) {
                        //	alias
                        return alias;
                } else {
                        //non alias
                        if (xpath==null)
                                return identifier.getMonitoringValue();
                        else
                                return (String)identifier.getMonitoringValue()+xpath.getMonitoringValue();
                }
        }

        public String getAllXpath() throws WSCoLException {
                if (extraPath != null)
                        return getMonitoringValue()+extraPath;
                else
                        return (String)getMonitoringValue();

        }
        @Override
        public String toString(){
                return "Variable";
        }

        public String getIdentifier() throws WSCoLException{
                return identifier.getMonitoringValue();
        }

}
