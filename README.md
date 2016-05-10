# FluVal


It is a light-weight Fluent Validation framework for java, using which one can write English-statement-like-validations:

e.g.

validator.valueOfField("name", person.getName()).shouldNotBeBlank();

validator.valueOfField("age", person.getAge()).shouldBeNumeric();

validator.valueOfField("age", person.getAge()).shouldBeInInclusiveRange(18,60);

validator.valueOfField("sex", person.getSex()).shouldBeOneOfInAnyCase("Male","Female");

validator.ifValueOfField("sex", person.getSex()).isInAnyCase("male").thenValueOfField(person.getAge()).shouldBeGreaterThan(21);

The validation results are propagated to the listener, which can take appropriate actions.

Please see the https://github.com/sushanted/FluVal/blob/master/src/test/java/validation/ValidationExample.java (An example which uses validator and created a simple report on sysout) and https://github.com/sushanted/FluVal/blob/master/src/test/resources/ValidationExampleResults (Result of ValidationExample.java's sample run) for more details.
