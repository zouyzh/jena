/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.jena.sdb.layout2.index;
/* SAP contribution from Fergal Monaghan (m#)/May 2012 */
import org.apache.jena.sdb.layout2.TableDescNodes ;
import org.apache.jena.sdb.sql.SDBConnection ;
import org.apache.jena.sdb.store.TableDesc ;

public class TupleLoaderIndexSAP extends TupleLoaderIndexBase {

	public TupleLoaderIndexSAP(SDBConnection connection, TableDesc tableDesc,
			int chunkSize) {
		super(connection, tableDesc, chunkSize);
	}
	
	@Override
    public String[] getNodeColTypes() {
		return new String[] {"BIGINT", "NVARCHAR(5000)", "NVARCHAR(10)", "NVARCHAR("+ TableDescNodes.DatatypeUriLength+ ")", "INT"};
	}
	
	@Override
    public String getTupleColType() {
		return "BIGINT";
	}
	
	@Override
    public String[] getCreateTempTable() {
		return new String[] { "CREATE GLOBAL TEMPORARY TABLE" , "" };
	}
	
	@Override
	public boolean clearsOnCommit() { return true; }
        
        @Override
        public String getLoadNodes() {
        	StringBuilder stmt = new StringBuilder();
        	stmt.append("INSERT INTO Nodes \nSELECT nodeid.nextval , "); // Autoindex thingy
        	for (int i = 0; i < getNodeColTypes().length; i++) {
        		if (i != 0) stmt.append(" , ");
        			stmt.append(getNodeLoader()).append(".").append("n").append(i);
        	}
        	stmt.append("\nFROM ").append(getNodeLoader()).append(" LEFT JOIN Nodes ON (");
        	stmt.append(getNodeLoader()).append(".n0=Nodes.hash) \nWHERE Nodes.hash IS NULL"); 
        	return stmt.toString();
        }
}
