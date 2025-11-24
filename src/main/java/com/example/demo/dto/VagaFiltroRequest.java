    package com.example.demo.dto;

    import java.util.List;

    public class VagaFiltroRequest {

        private List<Filtro> filters;

        public List<Filtro> getFilters() {
            return filters;
        }

        public void setFilters(List<Filtro> filters) {
            this.filters = filters;
        }

        public static class Filtro {

            public String title;
            public String area;
            public String company;
            public String state;
            public String city;
            public SalaryRange salary_range;

            public static class SalaryRange {

                public Double min;
                public Double max;
            }
        }
    }
