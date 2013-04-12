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

package it.polimi.monitor.nodes.binary;

import it.polimi.exception.WSCoLException;
import it.polimi.exception.WSCoLCastException;

import antlr.Token;

public class GLOperatorNode extends BinaryNode {

        private static final long serialVersionUID = -3630358764383397353L;

        private final String LT="<";
        private final String LTE="<=";
        private final String GT=">";
        private final String GTE=">=";
        private String op;

        private int operator;

        private final int LT_OPERATOR=0;
        private final int LTE_OPERATOR=1;
        private final int GT_OPERATOR=2;
        private final int GTE_OPERATOR=3;

        /**
         *
         */
        public GLOperatorNode(Token tok) {
                op=tok.getText();
                operator=operatorType(op);
                serializeTag="op";
        }
        @Override
        public Object getMonitoringValue() throws WSCoLException {
                logger.info("Start getMonitoringValue "+serializeTag);
                Boolean res;
                switch(operator){
                        case LT_OPERATOR:
                                res= ltEvaluate();
                                break;
                        case LTE_OPERATOR:
                                res= lteEvaluate();
                                break;
                        case GT_OPERATOR:
                                res= gtEvaluate();
                                break;
                        case GTE_OPERATOR:
                                res= gteEvaluate();
                                break;
                        default:
                                logger.severe("WSCoLCastExceprion");
                                throw new WSCoLCastException("Can't define instace of terms");
                }
                logger.info("Finish getMonitoringValue "+serializeTag+" result: "+res);
                return res;
        }
        private int operatorType(String s){
                if (s.equals(LT))
                        return LT_OPERATOR;
                else if (s.equals(LTE))
                        return LTE_OPERATOR;
                else if (s.equals(GT))
                        return GT_OPERATOR;
                else if (s.equals(GTE))
                        return GTE_OPERATOR;
                else
                        return -1;
        }

        private Boolean ltEvaluate() throws WSCoLCastException {
                switch (instace) {
                        case NUMBER:
                                if ( dLeft < dRight )
                                        return new Boolean(true);
                                else
                                        return new Boolean(false);
                        case STRING:
                                int temp=sLeft.compareTo(sRight);
                                if (temp <= 0)
                                        return new Boolean(true);
                                else
                                        return new Boolean(false);
                        case BOOLEAN: //non si può fare gt di booleani
                                logger.severe("WSCoLCastExceprion");
                                throw new WSCoLCastException("Can't define order beetwen boolean");
                        default:
                                //caso inverosimile
                                return null;
                        }
        }
        private Boolean lteEvaluate() throws WSCoLCastException {
                switch (instace) {
                        case NUMBER:
                                if ( dLeft <= dRight )
                                        return new Boolean(true);
                                else
                                        return new Boolean(false);
                        case STRING:
                                int temp=sLeft.compareTo(sRight);
                                if (temp <= 0)
                                        return new Boolean(true);
                                else
                                        return new Boolean(false);
                        case BOOLEAN: //non si può fare gt di booleani
                                logger.severe("WSCoLCastExceprion");
                                throw new WSCoLCastException("Can't define order beetwen boolean");
                        default:
                                // caso inverosimile
                                return null;
                }
        }

        private Boolean gtEvaluate() throws WSCoLCastException {
                switch (instace) {
                        case NUMBER:
                                if ( dLeft > dRight )
                                        return new Boolean(true);
                                else
                                        return new Boolean(false);
                        case STRING:
                                int temp=sLeft.compareTo(sRight);
                                if (temp >= 0)
                                        return new Boolean(true);
                                else
                                        return new Boolean(false);
                        case BOOLEAN: //non si può fare gt di booleani
                                logger.severe("WSCoLCastExceprion");
                                throw new WSCoLCastException("Can't define order beetwen boolean");
                        default:
                                //      caso inverosimile
                                return null;
                }
        }

        private Boolean gteEvaluate() throws WSCoLCastException{
                switch (instace) {
                        case NUMBER:
                                if ( dLeft >= dRight )
                                        return new Boolean(true);
                                else
                                        return new Boolean(false);
                        case STRING:
                                int temp=sLeft.compareTo(sRight);
                                if (temp >= 0)
                                        return new Boolean(true);
                                else
                                        return new Boolean(false);
                        case BOOLEAN: //non si può fare gt di booleani
                                logger.severe("WSCoLCastExceprion");
                                throw new WSCoLCastException("Can't define order beetwen boolean");
                        default:
                                // caso inverosimile
                                return null;
                }
        }
        @Override
        public String toString(){
                return op;
        }
}
