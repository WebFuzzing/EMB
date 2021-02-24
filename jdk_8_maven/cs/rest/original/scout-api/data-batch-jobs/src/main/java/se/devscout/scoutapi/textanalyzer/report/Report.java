package se.devscout.scoutapi.textanalyzer.report;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement
public class Report {

    @XmlRootElement
    public static class Activity extends Entity {
        @XmlElement
        public
        String name;
        @XmlElement
        public
        List<Relation> relations = new ArrayList<>();

        private Activity() {
        }

        public Activity(String name, long id) {
            this.name = name;
            this.id = id;
        }

        public void add(Relation relation) {
            relations.add(relation);
        }

        @XmlRootElement
        public static class Relation extends Entity {
            @XmlElement
            String name;
            @XmlElement(name = "v")
            @XmlElementWrapper(name = "comparatorValues")
            String[] comparatorValues;

            private Relation() {
            }

            public Relation(String[] comparatorValues, String name, long id) {
                this.comparatorValues = comparatorValues;
                this.name = name;
                this.id = id;
            }
        }
    }

    @XmlElement(name = "v")
    @XmlElementWrapper(name = "comparatorValuesLabels")
    public
    String[] comparatorValuesLabels;

    @XmlElement
    public
    List<Activity> activities = new ArrayList<>();

    public static class Entity {
        @XmlElement
        public long id;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Entity)) return false;

            Entity entity = (Entity) o;

            if (id != entity.id) return false;

            return true;
        }

        @Override
        public int hashCode() {
            return (int)id;
        }
    }
}
