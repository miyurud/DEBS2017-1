package org.wso2.siddhi.debs2017.input;

import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import org.apache.jena.riot.RDFDataMgr;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


/*
* Copyright (c) 2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
public class SparQLProcessor {
    private final static String queryString = " PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
            "PREFIX wmm: <http://www.agtinternational.com/ontologies/WeidmullerMetadata#>" +
            "PREFIX debs:<http://project-hobbit.eu/resources/debs2017#>" +
            "PREFIX ssn: <http://purl.oclc.org/NET/ssnx/ssn#>" +
            "PREFIX IoTCore: <http://www.agtinternational.com/ontologies/IoTCore#>" +
            "PREFIX i40: <http://www.agtinternational.com/ontologies/I4.0#>" +
            "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>" +
            "SELECT ?machine ?time ?timeStamp ?value ?property WHERE { " +
            "?a rdf:type i40:MoldingMachineObservationGroup ." +
            "?a i40:machine ?machine .  " +
            "?a ssn:observationResultTime ?time ." +
            "?time IoTCore:valueLiteral ?timeStamp ." +
            "?a i40:contains ?b ." +
            "?b ssn:observationResult ?c ." +
            "?c ssn:hasValue ?d ." +
            "?b ssn:observedProperty ?property ." +
            "?d  IoTCore:valueLiteral ?value" +
            "}" +
            "ORDER BY ASC (?timeStamp)";

    /**
     * @param fileName the file which contains RDF triples
     */
    public static void excuteQuery(String fileName) {

        try {

            FileWriter writer = new FileWriter(new File(fileName));
            Model model = RDFDataMgr.loadModel("molding_machine_100M_rdf.ttl");
            Query query = QueryFactory.create(queryString);
                QueryExecution qexec = QueryExecutionFactory.create(query, model);
                    ResultSet results = qexec.execSelect();
                    results = ResultSetFactory.copyResults(results);
                    for (; results.hasNext(); ) {
                       QuerySolution solution = results.nextSolution();
                        Resource time = solution.getResource("time"); // Get a result variable - must be a resource
                        Resource property = solution.getResource("property");
                        Resource machine = solution.getResource("machine");
                        Literal value = solution.getLiteral("value");
                        if (!value.toString().contains("#string")) {
                           writer.write(machine.getLocalName() + "," + time.getLocalName() + "," + property.getLocalName() + "," + value.getFloat() + "\n");
                        }
                    }
            writer.flush();
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
