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

public interface WSCoLFunction {
	public static final int LENGTH = 1;
	public static final int COMPARE= 2;
	public static final int REPLACE= 3;
	public static final int SUBSTRING= 4;
	public static final int CONTAINS= 5;
	public static final int STARTWITH= 6;
	public static final int ENDWITH= 7;
	public static final int SERIALIZE= 8;
	public static final int SIZE= 9;
	public static final int ABS=10;
	public static final int CEILING=11;
	public static final int FLOOR=12;
	public static final int ROUND=13;
	
	public final String VALUE_LENGTH="length";
	public final String VALUE_COMPARE="compare";
	public final String VALUE_REPLACE="replace";
	public final String VALUE_SUBSTRING="substring";
	public final String VALUE_CONTAINS="contains";
	public final String VALUE_STARTWITH="startsWith";
	public final String VALUE_ENDWITH="endsWith";
	public final String VALUE_SERIALIZE="serialize";
	public final String VALUE_SIZE="size";
	public final String VALUE_ABS="abs";
	public final String VALUE_CEILING="ceiling";
	public final String VALUE_FLOOR="floor";
	public final String VALUE_ROUND="round";
	
}
