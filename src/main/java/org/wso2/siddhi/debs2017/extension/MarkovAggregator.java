package org.wso2.siddhi.debs2017.extension;

import org.wso2.siddhi.core.config.ExecutionPlanContext;
import org.wso2.siddhi.core.executor.ExpressionExecutor;
import org.wso2.siddhi.core.query.selector.attribute.aggregator.AttributeAggregator;
import org.wso2.siddhi.debs2017.markovchain.MarkovModel;
import org.wso2.siddhi.debs2017.markovchain.MarkoveModified;
import org.wso2.siddhi.query.api.definition.Attribute;


import java.util.ArrayList;

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
public class MarkovAggregator extends AttributeAggregator {

    /**
     * arraylist to maintain the events in the current window
     */
    private ArrayList<Integer> arr = new ArrayList<>();
     private  int windowfull = 0;
     private  double probability;
     MarkoveModified markoveModified = new MarkoveModified();
    @Override
    protected void init(ExpressionExecutor[] expressionExecutors, ExecutionPlanContext executionPlanContext) {

    }

    @Override
    public Attribute.Type getReturnType() {
        return null;
    }

    @Override
    public Object processAdd(Object data) {
        arr.add((int) data);
        if(windowfull > 0){
          //  System.out.println(arr);
            probability = markoveModified.updateProbability(arr);
            markoveModified.execute((int)data);

        }else{
            markoveModified.execute((int)data);
           // System.out.println(arr);
            probability = -1;
        }
        return probability;




    }

    @Override
    public Object processAdd(Object[] objects) {

        return null;
    }

    @Override
    /**
     * remove  expired events from arraylist
     * reduce the event transition count for the expired event
     * */
    public Object processRemove(Object o) {
        windowfull++;
        arr.remove(0);
        if (arr.size() >= 2)

           markoveModified.reduceCount((int) o, arr.get(0));


        return null;
    }

    @Override
    public Object processRemove(Object[] objects) {
        return null;
    }

    @Override
    public Object reset() {
        return null;
    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

    @Override
    public Object[] currentState() {
        return new Object[0];
    }

    @Override
    public void restoreState(Object[] objects) {

    }
}
