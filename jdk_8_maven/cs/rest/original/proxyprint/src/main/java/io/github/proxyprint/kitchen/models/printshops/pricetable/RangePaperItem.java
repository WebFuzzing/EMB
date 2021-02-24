/*
 * Copyright 2016 Pivotal Software, Inc..
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.proxyprint.kitchen.models.printshops.pricetable;

/**
 *
 * @author josesousa
 */
public class RangePaperItem extends PaperItem {
    private int infLim;
    private int supLim;

    public RangePaperItem(Format format, Sides sides, Colors colors, int infLim, int supLim) {
        super(format,sides,colors);
        this.infLim = infLim;
        this.supLim = supLim;
    }

    public int getInfLim() {
        return infLim;
    }

    public void setInfLim(int infLim) {
        this.infLim = infLim;
    }

    public int getSupLim() {
        return supLim;
    }

    public void setSupLim(int supLim) {
        this.supLim = supLim;
    }

    @Override
    public String genKey() {
        return String.format("%s,%s,%s,%s,%d,%d",KEY_BASE, this.colors, this.format, this.sides, infLim, supLim);
    }

}
