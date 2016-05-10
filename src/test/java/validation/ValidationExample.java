package validation;

import java.util.regex.Pattern;

import validation.Validator.PreCondition;
import validation.Validator.Result;
import validation.Validator.Status;



public class ValidationExample {

    public static class MyValidationListener implements ValidationListener {

        public static void main(String[] args) {

            Validator validator = new Validator();

            validator.setValidationListener(new MyValidationListener());

            Person person = new Person().setName("pater").setAddress("")
                    .setSalary(20000).setAge(45).setSex("male");

            validator.valueOfField("Salary", person.getSalary())
                    .shouldBeGreaterThan(20000l);
            
            validator.valueOfField("Name", person.getName()).shouldNotBeBlank();
            
            validator.valueOfField("Address", person.getAddress())
                    .shouldNotBeBlank();
            
            validator.valueOfField("Age", person.getAge())
                    .shouldBeInInclusiveRange(21, 50);

            validator.ifValueOfField("Sex", person.getSex()).is("female")
                    .thenValueOfField("Age", person.getAge())
                    .shouldBeLessThan(40);
            
            validator.valueOfField("Phone", person.getPhone()).shouldMatch(Pattern.compile("\\d+"), "syntax: only digits.");

        }

        @Override
        public void validated(Result<?> result) {

            System.out.println("\nValidation: " + result.getStatus());

            if (result.getStatus() == Status.PreConditionNotMet) {
                PreCondition<?> preCondition = result.getCondition()
                        .getPreResult().getPreCondition();
                System.out.println("Pre condition:"
                        + preCondition.getFieldName() + " "
                        + result.getCondition().getPreResult().getType() + " "
                        + preCondition.getFieldValue());
            }

            System.out
                    .println("\tExpected : "
                            + result.getFieldName()
                            + " "
                            + result.getType()
                            + " "
                            + (result.getComparedValue() == null ? "" : result
                                    .getComparedValue())
                            + ((result.getFromRange() != null && result
                                    .getToRange() != null) ? " ("
                                    + result.getFromRange() + ","
                                    + result.getToRange() + ")" : "")
                                    
                                    +(result.getSyntax() == null ? "":result.getSyntax())
                                    
                            + "\n\tActual   : " + result.getFieldValue() );

        }

    }

    public static class Person {

        private String name;
        private String address;
        private long salary;
        private String sex;
        private int age;
        private String phone;

        public String getName() {
            return name;
        }

        public Person setName(String name) {
            this.name = name;
            return this;
        }

        public String getAddress() {
            return address;
        }

        public Person setAddress(String address) {
            this.address = address;
            return this;
        }

        public long getSalary() {
            return salary;
        }

        public Person setSalary(long salary) {
            this.salary = salary;
            return this;
        }

        public String getSex() {
            return sex;
        }

        public Person setSex(String sex) {
            this.sex = sex;
            return this;
        }

        public int getAge() {
            return age;
        }

        public Person setAge(int age) {
            this.age = age;
            return this;
        }

        public String getPhone() {
            return phone;
        }

        public Person setPhone(String phone) {
            this.phone = phone;
            return this;
        }
        
        

    }

}
