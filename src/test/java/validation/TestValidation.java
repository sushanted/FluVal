package validation;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Test;

import validation.Validator.PreConditionType;
import validation.Validator.PreResult;
import validation.Validator.Result;
import validation.Validator.Status;
import validation.Validator.ValidationType;


public class TestValidation {

	private Validator validator = new Validator();

	public static class ValidationListenerForTest implements ValidationListener {

		private Result<?> result;

		@Override
		public void validated(Result<?> result) {
			this.result = result;
		}

		public Result<?> getResult() {
			return result;
		}

		public void setResult(Result<?> result) {
			this.result = result;
		}

	};

	private ValidationListenerForTest validationListener = new ValidationListenerForTest();

	{
		validator.setValidationListener(validationListener);
	}

	@Test
	public void testNullCheckPassed() {
		String name = "Name";
		String value = null;

		validator.valueOfField(name, value).shouldBeNull();

		assertOutput(name, value, Status.Passed, ValidationType.ShouldBeNull);
	}

	@Test
	public void testNullCheckFailed() {
		String name = "Name";
		String value = "nonNull";

		validator.valueOfField(name, value).shouldBeNull();

		assertOutput(name, value, Status.Failed, ValidationType.ShouldBeNull);
	}

	@Test
	public void testNotNullCheckPassed() {
		String name = "Name";
		String value = "nonNull";

		validator.valueOfField(name, value).shouldNotBeNull();

		assertOutput(name, value, Status.Passed, ValidationType.ShouldNotBeNull);
	}

	@Test
	public void testNotNullCheckFailed() {
		String name = "Name";
		String value = null;

		validator.valueOfField(name, value).shouldNotBeNull();

		assertOutput(name, value, Status.Failed, ValidationType.ShouldNotBeNull);
	}

	@Test
	public void testNotBlankCheckFailedByNull() {

		String name = "Name";
		String value = null;

		validator.valueOfField(name, value).shouldNotBeBlank();

		assertOutput(name, value, Status.Failed,
				ValidationType.ShouldNotBeBlank);
	}

	@Test
	public void testNotBlankCheckFailedByEmpty() {

		String name = "Name";
		String value = " ";

		validator.valueOfField(name, value).shouldNotBeBlank();

		assertOutput(name, value, Status.Failed,
				ValidationType.ShouldNotBeBlank);
	}

	@Test
	public void testNotBlankCheckPassed() {

		String name = "Name";
		String value = "amit";

		validator.valueOfField(name, value).shouldNotBeBlank();

		assertOutput(name, value, Status.Passed,
				ValidationType.ShouldNotBeBlank);
	}

	@Test
	public void testBlankCheckPassedByNull() {

		String name = "Name";
		String value = null;

		validator.valueOfField(name, value).shouldBeBlank();

		assertOutput(name, value, Status.Passed, ValidationType.ShouldBeBlank);
	}

	@Test
	public void testBlankCheckPassedByEmpty() {

		String name = "Name";
		String value = " ";

		validator.valueOfField(name, value).shouldBeBlank();

		assertOutput(name, value, Status.Passed, ValidationType.ShouldBeBlank);
	}

	@Test
	public void testBlankCheckNullValue() {

		String name = "Name";
		String value = "amit";

		validator.valueOfField(name, value).shouldBeBlank();

		assertOutput(name, value, Status.Failed, ValidationType.ShouldBeBlank);
	}

	@Test
	public void testNumericCheckNullValue() {

		String name = "Name";
		String value = null;

		validator.valueOfField(name, value).shouldBeNumeric();

		assertOutput(name, value, Status.NullValueOperation,
				ValidationType.ShouldBeNumeric);
	}
	
	@Test
	public void testNumericCheckPassed() {

		String name = "Name";
		String value = "45";

		validator.valueOfField(name, value).shouldBeNumeric();

		assertOutput(name, value, Status.Passed, ValidationType.ShouldBeNumeric);
	}

	@Test
	public void testNumericCheckFailed() {

		String name = "Name";
		String value = "4s5";

		validator.valueOfField(name, value).shouldBeNumeric();

		assertOutput(name, value, Status.Failed, ValidationType.ShouldBeNumeric);
	}

	@Test
	public void testBooleanCheckNullValue() {

		String name = "Name";
		String value = null;

		validator.valueOfField(name, value).shouldBeBoolean();

		assertOutput(name, value, Status.NullValueOperation,
				ValidationType.ShouldBeBoolean);
	}

	@Test
	public void testBooleanCheckPassed() {

		String name = "Name";
		String value = "true";

		validator.valueOfField(name, value).shouldBeBoolean();

		assertOutput(name, value, Status.Passed, ValidationType.ShouldBeBoolean);
	}
	
	@Test
	public void testBooleanCheckFailed() {

		String name = "Name";
		String value = "false123";

		validator.valueOfField(name, value).shouldBeBoolean();

		assertOutput(name, value, Status.Failed, ValidationType.ShouldBeBoolean);
	}

	@Test
	public void testMandateFailed() {
		String name = "Name";
		String value = "";

		validator.mandateField(name, value);

		assertOutput(name, value, Status.Failed,
				ValidationType.ShouldNotBeBlank);
	}

	@Test
	public void testMultiMandateFailed() {
		String name = "Name";
		String value = "";

		validator.mandateField(name, null).mandateField(name, value);

		assertOutput(name, value, Status.Failed,
				ValidationType.ShouldNotBeBlank);
	}
	
	@Test
	public void testMatchPassed() {
		String name = "code";
		String value = "A20";

		String syntax = "A-Z followed by a number";
		
		validator.valueOfField(name, value).shouldMatch(Pattern.compile("[A-Z]\\d+"), syntax);

		assertSyntaxOutput(name, value, Status.Passed, ValidationType.ShouldMatch,syntax);
	}
	
	
	@Test
	public void testMatchFailed() {
		String name = "code";
		String value = "a20";

		String syntax = "A-Z followed by a number";
		
		validator.valueOfField(name, value).shouldMatch(Pattern.compile("[A-Z]\\d+"), syntax);

		assertSyntaxOutput(name, value, Status.Failed, ValidationType.ShouldMatch,syntax);
	}
	
	
	@Test
	public void testMatchesNullValue() {
		
		String name = "code";
		String value = null;

		String syntax = "A-Z followed by a number";
		
		validator.valueOfField(name, value).shouldMatch(Pattern.compile("[A-Z]\\d+"), syntax);

		assertSyntaxOutput(name, value, Status.NullValueOperation, ValidationType.ShouldMatch,syntax);
	}
	
	@Test
	public void testExistingFilePassed() {
		String name = "fileName";
		
		String filePath = TestValidation.class.getName().replace(".", "/")+".class";
		
		String value = TestValidation.class.getClassLoader().getResource(filePath).getPath();

		validator.valueOfField(name, value).shouldBeAnExistingFile();

		assertOutput(name, value, Status.Passed, ValidationType.ShouldBeAnExistingFile);
	}
	
	
	@Test
	public void testExistingFileFailed() {
		String name = "fileName";
		String value = "c:\\thisdoesntexistatall_";
		
		validator.valueOfField(name, value).shouldBeAnExistingFile();

		assertOutput(name, value, Status.Failed, ValidationType.ShouldBeAnExistingFile);
	}
	
	
	@Test
	public void testExistingFileNullValue() {
		
		String name = "code";
		String value = null;

		validator.valueOfField(name, value).shouldBeAnExistingFile();

		assertOutput(name, value, Status.NullValueOperation, ValidationType.ShouldBeAnExistingFile);
	}
	
	
	@Test
	public void testIsFileExistPassed() {
		
		String precoditionFieldName = "fileName";
		
		String filePath = TestValidation.class.getName().replace(".", "/")+".class";
		
		String preCoditionValue = TestValidation.class.getClassLoader().getResource(filePath).getPath();
		
		String fieldName = "Name";
		String fieldValue = "Amit";
		
		validator.ifValueOfField(precoditionFieldName, preCoditionValue).isAnExistingFile()
				.thenValueOfField(fieldName, fieldValue).shouldBe("Amit");
		
		
		assertPreConditionOutput(fieldName, fieldValue,
				ValidationType.ShouldBe, Status.Passed,
				precoditionFieldName, preCoditionValue,PreConditionType.IsAnExistingFile);
	}
	
	@Test
	public void testIsFileExistPreCondtitionNotMet() {
		
		String precoditionFieldName = "fileName";
		
		String preCoditionValue = "c:\\thisfilewill_neve_exist";
		
		String fieldName = "Name";
		String fieldValue = "Amit";
		
		validator.ifValueOfField(precoditionFieldName, preCoditionValue).isAnExistingFile()
				.thenValueOfField(fieldName, fieldValue).shouldBe("Amit");
		
		
		assertPreConditionOutput(fieldName, fieldValue,
				ValidationType.ShouldBe, Status.PreConditionNotMet,
				precoditionFieldName, preCoditionValue,PreConditionType.IsAnExistingFile);
	}
	
	@Test
	public void testIsFileExistPreCondtitionNull() {
		
		String precoditionFieldName = "fileName";
		
		String preCoditionValue = null;
		
		String fieldName = "Name";
		String fieldValue = "Amit";
		
		validator.ifValueOfField(precoditionFieldName, preCoditionValue).isAnExistingFile()
				.thenValueOfField(fieldName, fieldValue).shouldBe("Amit");
		
		
		assertPreConditionOutput(fieldName, fieldValue,
				ValidationType.ShouldBe, Status.PreConditionNotMet,
				precoditionFieldName, preCoditionValue,PreConditionType.IsAnExistingFile);
	}
	

	@Test
	public void testShouldBeGreaterThanNullValue() {

		String name = "Name";
		Object value = null;

		validator.valueOfField(name, value).shouldBeGreaterThan(value);

		assertCompareOutput(name, value, value, Status.NullValueOperation,
				ValidationType.ShouldBeGreaterThan);
	}

	@Test
	public void testShouldBeGreaterThanIncompatible() {

		String name = "Name";
		Object value = new Object();

		validator.valueOfField(name, value).shouldBeGreaterThan(value);

		assertCompareOutput(name, value, value, Status.IncompatibleType,
				ValidationType.ShouldBeGreaterThan);
	}

	@Test
	public void testShouldBeGreaterThanPassed() {

		String name = "Age";
		int value = 25;
		int compareWithValue = 24;

		validator.valueOfField(name, value).shouldBeGreaterThan(compareWithValue);

		assertCompareOutput(name, value, compareWithValue, Status.Passed,
				ValidationType.ShouldBeGreaterThan);
	}

	@Test
	public void testShouldBeGreaterThanFailed() {

		String name = "Age";
		int value = 24;
		int compareWithValue = 24;

		validator.valueOfField(name, value).shouldBeGreaterThan(compareWithValue);

		assertCompareOutput(name, value, compareWithValue, Status.Failed,
				ValidationType.ShouldBeGreaterThan);
	}

	@Test
	public void testShouldBeLessThanNullValue() {

		String name = "Name";
		Object value = null;

		validator.valueOfField(name, value).shouldBeLessThan(value);

		assertCompareOutput(name, value, value, Status.NullValueOperation,
				ValidationType.ShouldBeLessThan);
	}

	@Test
	public void testShouldBeLessThanIncompatible() {

		String name = "Name";
		Object value = new Object();

		validator.valueOfField(name, value).shouldBeLessThan(value);

		assertCompareOutput(name, value, value, Status.IncompatibleType,
				ValidationType.ShouldBeLessThan);
	}

	@Test
	public void testShouldBeLessThanPassed() {

		String name = "Age";
		int value = 24;
		int compareWithValue = 25;

		validator.valueOfField(name, value).shouldBeLessThan(compareWithValue);

		assertCompareOutput(name, value, compareWithValue, Status.Passed,
				ValidationType.ShouldBeLessThan);
	}

	@Test
	public void testShouldBeLessThanFailed() {

		String name = "Age";
		int value = 24;
		int compareWithValue = 24;

		validator.valueOfField(name, value).shouldBeLessThan(compareWithValue);

		assertCompareOutput(name, value, compareWithValue, Status.Failed,
				ValidationType.ShouldBeLessThan);
	}

	
	
	@Test
	public void testInInclusiveRangeNullValue() {

		String name = "Name";
		Object value = null;
		Object fromRange = "A";
		Object toRange = "Z";

		validator.valueOfField(name, value).shouldBeInInclusiveRange(fromRange,toRange);

		assertRangeOutput(name, value, fromRange,toRange, Status.NullValueOperation,
				ValidationType.ShouldBeInInclusiveRange);
	}

	@Test
	public void testInInclusiveRangeIncompatible() {

		String name = "Name";
		Object value = new Object();
		Object fromRange = "A";
		Object toRange = "Z";

		validator.valueOfField(name, value).shouldBeInInclusiveRange(fromRange,toRange);

		assertRangeOutput(name, value, fromRange,toRange, Status.IncompatibleType,
				ValidationType.ShouldBeInInclusiveRange);
	}
	
	@Test
	public void testInInclusiveRangePassed() {

		String name = "Name";
		Object value = "M";
		Object fromRange = "A";
		Object toRange = "Z";

		validator.valueOfField(name, value).shouldBeInInclusiveRange(fromRange,toRange);

		assertRangeOutput(name, value, fromRange,toRange, Status.Passed,
				ValidationType.ShouldBeInInclusiveRange);
	}
	
	
	@Test
	public void testInInclusiveRangeFailedInLowerRange() {

		String name = "Name";
		Object value = "A";
		Object fromRange = "B";
		Object toRange = "Y";

		validator.valueOfField(name, value).shouldBeInInclusiveRange(fromRange,toRange);

		assertRangeOutput(name, value, fromRange,toRange, Status.Failed,
				ValidationType.ShouldBeInInclusiveRange);
	}
	
	
	@Test
	public void testInInclusiveRangeFailedInUpperRange() {

		String name = "Name";
		Object value = "Z";
		Object fromRange = "B";
		Object toRange = "Y";

		validator.valueOfField(name, value).shouldBeInInclusiveRange(fromRange,toRange);

		assertRangeOutput(name, value, fromRange,toRange, Status.Failed,
				ValidationType.ShouldBeInInclusiveRange);
	}

	
	
	@Test
	public void testInExclusiveRangeNullValue() {

		String name = "Name";
		Object value = null;
		Object fromRange = "A";
		Object toRange = "Z";

		validator.valueOfField(name, value).shouldBeInExclusiveRange(fromRange,toRange);

		assertRangeOutput(name, value, fromRange,toRange, Status.NullValueOperation,
				ValidationType.ShouldBeInExclusiveRange);
	}

	@Test
	public void testInExclusiveRangeIncompatible() {

		String name = "Name";
		Object value = new Object();
		Object fromRange = "A";
		Object toRange = "Z";

		validator.valueOfField(name, value).shouldBeInExclusiveRange(fromRange,toRange);

		assertRangeOutput(name, value, fromRange,toRange, Status.IncompatibleType,
				ValidationType.ShouldBeInExclusiveRange);
	}
	
	@Test
	public void testInExclusiveRangePassed() {

		String name = "Name";
		Object value = "M";
		Object fromRange = "A";
		Object toRange = "Z";

		validator.valueOfField(name, value).shouldBeInExclusiveRange(fromRange,toRange);

		assertRangeOutput(name, value, fromRange,toRange, Status.Passed,
				ValidationType.ShouldBeInExclusiveRange);
	}
	
	
	@Test
	public void testInExclusiveRangeFailedInLowerRange() {

		String name = "Name";
		Object value = "A";
		Object fromRange = "A";
		Object toRange = "Z";

		validator.valueOfField(name, value).shouldBeInExclusiveRange(fromRange,toRange);

		assertRangeOutput(name, value, fromRange,toRange, Status.Failed,
				ValidationType.ShouldBeInExclusiveRange);
	}
	
	
	@Test
	public void testInExclusiveRangeFailedInUpperRange() {

		String name = "Name";
		Object value = "Z";
		Object fromRange = "A";
		Object toRange = "Z";

		validator.valueOfField(name, value).shouldBeInExclusiveRange(fromRange,toRange);

		assertRangeOutput(name, value, fromRange,toRange, Status.Failed,
				ValidationType.ShouldBeInExclusiveRange);
	}
	
	@Test
	public void testShouldBeOneOfNullValue() {

		String name = "Name";
		Object value = null;
		
		Object[] domainValues = { "North" , "South" , "East", "West" };

		validator.valueOfField(name, value).shouldBeOneOf(domainValues);

		assertOneOfOutput(name, value, Arrays.asList(domainValues), Status.NullValueOperation,
				ValidationType.ShouldBeOneOf);
	}
	
	@Test
	public void testShouldBeOneOfFailed() {

		String name = "Name";
		Object value = "NorthEast";
		
		Object[] domainValues = { "North" , "South" , "East", "West" };

		validator.valueOfField(name, value).shouldBeOneOf(domainValues);

		assertOneOfOutput(name, value, Arrays.asList(domainValues), Status.Failed,
				ValidationType.ShouldBeOneOf);
	}
	
	@Test
	public void testShouldBeOneOfPassed() {

		String name = "Name";
		Object value = "North";
		
		Object[] domainValues = { "North" , "South" , "East", "West" };

		validator.valueOfField(name, value).shouldBeOneOf(domainValues);

		assertOneOfOutput(name, value, Arrays.asList(domainValues), Status.Passed,
				ValidationType.ShouldBeOneOf);
	}
	
	@Test
	public void testShouldBeOneOfInAnyCaseNullValue() {

		String name = "Name";
		Object value = null;
		
		Object[] domainValues = { "North" , "South" , "East", "West" };

		validator.valueOfField(name, value).shouldBeOneOfInAnyCase(domainValues);

		assertOneOfOutput(name, value, Arrays.asList(domainValues), Status.NullValueOperation,
				ValidationType.ShouldBeOneOf);
	}
	
	@Test
	public void testShouldBeOneOfInAnyCaseFailed() {

		String name = "Name";
		Object value = "NorthEast";
		
		Object[] domainValues = { "North" , "South" , "East", "West" };

		validator.valueOfField(name, value).shouldBeOneOfInAnyCase(domainValues);

		assertOneOfOutput(name, value, Arrays.asList(domainValues), Status.Failed,
				ValidationType.ShouldBeOneOf);
	}
	
	@Test
	public void testShouldBeOneOfInAnyCasePassed() {

		String name = "Name";
		Object value = "NoRtH";
		
		Object[] domainValues = { "North" , "South" , "East", "West" };

		validator.valueOfField(name, value).shouldBeOneOfInAnyCase(domainValues);

		assertOneOfOutput(name, value, Arrays.asList(domainValues), Status.Passed,
				ValidationType.ShouldBeOneOf);
	}

	@Test
	public void testShouldBeOneOfCollectionNullValue() {

		String name = "Name";
		Object value = null;
		
		String[] domainValues = { "North" , "South" , "East", "West" };

		validator.valueOfField(name, value).shouldBeOneOf(new HashSet<Object>(Arrays.asList(domainValues)));

		assertOneOfOutput(name, value, Arrays.asList(domainValues), Status.NullValueOperation,
				ValidationType.ShouldBeOneOf);
	}
	
	
	
	
	@Test
	public void testShouldBeOneOfCollectionFailed() {

		String name = "Name";
		String value = "NorthEast";
		
		String[] domainValues = { "North" , "South" , "East", "West" };

		validator.valueOfField(name, value).shouldBeOneOf(new HashSet<String>(Arrays.asList(domainValues)));

		assertOneOfOutput(name, value, Arrays.asList(domainValues), Status.Failed,
				ValidationType.ShouldBeOneOf);
	}
	
	@Test
	public void testShouldBeOneOfCollectionPassed() {

		String name = "Name";
		String value = "North";
		
		String[] domainValues = { "North" , "South" , "East", "West" };

		validator.valueOfField(name, value).shouldBeOneOf(new HashSet<String>(Arrays.asList(domainValues)));

		assertOneOfOutput(name, value, Arrays.asList(domainValues), Status.Passed,
				ValidationType.ShouldBeOneOf);
	}
	
	
	
	@Test
	public void testShouldBeOneOfAnyCaseCollectionNullValue() {

		String name = "Name";
		Object value = null;
		
		String[] domainValues = { "North" , "South" , "East", "West" };

		validator.valueOfField(name, value).shouldBeOneOfInAnyCase(new HashSet<Object>(Arrays.asList(domainValues)));

		assertOneOfOutput(name, value, Arrays.asList(domainValues), Status.NullValueOperation,
				ValidationType.ShouldBeOneOf);
	}
	
	@Test
	public void testShouldBeOneOfAnyCaseCollectionFailed() {

		String name = "Name";
		String value = "NorthEast";
		
		String[] domainValues = { "North" , "South" , "East", "West" };

		validator.valueOfField(name, value).shouldBeOneOfInAnyCase(new HashSet<String>(Arrays.asList(domainValues)));

		assertOneOfOutput(name, value, Arrays.asList(domainValues), Status.Failed,
				ValidationType.ShouldBeOneOf);
	}
	
	@Test
	public void testShouldBeOneOfAnyCaseCollectionPassed() {

		String name = "Name";
		String value = "NoRtH";
		
		String[] domainValues = { "North" , "South" , "East", "West" };

		validator.valueOfField(name, value).shouldBeOneOfInAnyCase(new HashSet<String>(Arrays.asList(domainValues)));

		assertOneOfOutput(name, value, Arrays.asList(domainValues), Status.Passed,
				ValidationType.ShouldBeOneOf);
	}
	
	public static enum Direction{
		NORTH,EAST,SOUTH,WEST
	}
	
	@Test
	public void testShouldBeOneOfEnumPassed() {

		String name = "Name";
		String value = "NORTH";
		
		validator.valueOfField(name, value).shouldBeOneOfEnumNames(Direction.class);

		assertOneOfOutput(
				name,
				value,
				(Arrays.asList(Direction.values()).stream()
						.map(v -> String.valueOf(v))
						.collect(Collectors.toList())), Status.Passed,
				ValidationType.ShouldBeOneOf);
	}
	
	@Test
	public void testShouldBeOneOfEnumFailed() {

		String name = "Name";
		String value = "NORTHEAST";
		
		validator.valueOfField(name, value).shouldBeOneOfEnumNames(Direction.class);

		assertOneOfOutput(
				name,
				value,
				(Arrays.asList(Direction.values()).stream()
						.map(v -> String.valueOf(v))
						.collect(Collectors.toList())), Status.Failed,
				ValidationType.ShouldBeOneOf);
	}
	
	@Test
	public void testShouldBeOneOfEnumNullValue() {

		String name = "Name";
		Direction value = null;
		
		validator.valueOfField(name, value).shouldBeOneOfEnumNames(Direction.class);

		assertOneOfOutput(name, value, Arrays.asList(Direction.values()).stream()
				.map(v -> String.valueOf(v))
				.collect(Collectors.toList()), Status.NullValueOperation,
				ValidationType.ShouldBeOneOf);
	}
	
	
	@Test
	public void testShouldBeOneOfEnumInAnyCasePassed() {

		String name = "Name";
		String value = "nOrTh";
		
		validator.valueOfField(name, value).shouldBeOneOfEnumNamesInAnyCase(Direction.class);

		assertOneOfOutput(
				name,
				value,
				(Arrays.asList(Direction.values()).stream()
						.map(v -> String.valueOf(v))
						.collect(Collectors.toList())), Status.Passed,
				ValidationType.ShouldBeOneOf);
	}
	
	@Test
	public void testShouldBeOneOfEnumInAnyCaseFailed() {

		String name = "Name";
		String value = "NORTHEAST";
		
		validator.valueOfField(name, value).shouldBeOneOfEnumNamesInAnyCase(Direction.class);

		assertOneOfOutput(
				name,
				value,
				(Arrays.asList(Direction.values()).stream()
						.map(v -> String.valueOf(v))
						.collect(Collectors.toList())), Status.Failed,
				ValidationType.ShouldBeOneOf);
	}
	
	@Test
	public void testShouldBeOneOfEnumInAnyCaseNullValue() {

		String name = "Name";
		Direction value = null;
		
		validator.valueOfField(name, value).shouldBeOneOfEnumNamesInAnyCase(Direction.class);

		assertOneOfOutput(name, value, Arrays.asList(Direction.values()).stream()
				.map(v -> String.valueOf(v))
				.collect(Collectors.toList()), Status.NullValueOperation,
				ValidationType.ShouldBeOneOf);
	}
	
	@Test
	public void testIsPassed() {
		
		String fieldName = "Name";
		String fieldValue = "Amit";
		String precoditionFieldName = "Age";
		int preCoditionValue = 30;
		
		validator.ifValueOfField(precoditionFieldName, preCoditionValue).is(30)
				.thenValueOfField(fieldName, fieldValue).shouldBe("Amit");
		
		
		assertPreConditionOutput(fieldName, fieldValue,
				ValidationType.ShouldBe, Status.Passed,
				precoditionFieldName, preCoditionValue,PreConditionType.Is);
	}
	
	@Test
	public void testIsInAnyCasePassed() {
		
		String fieldName = "Name";
		String fieldValue = "Amit";
		String precoditionFieldName = "Age";
		String preCoditionValue = "value";
		
		validator.ifValueOfField(precoditionFieldName, preCoditionValue).isInAnyCase("VaLuE")
				.thenValueOfField(fieldName, fieldValue).shouldBe("Amit");
		
		
		assertPreConditionOutput(fieldName, fieldValue,
				ValidationType.ShouldBe, Status.Passed,
				precoditionFieldName, preCoditionValue,PreConditionType.Is);
	}
	
	@Test
	public void testIsInAnyCasePreConditionNotMet() {
		
		String fieldName = "Name";
		String fieldValue = "Amit";
		String precoditionFieldName = "Age";
		String preCoditionValue = "value";
		
		validator.ifValueOfField(precoditionFieldName, preCoditionValue).isInAnyCase("value1")
				.thenValueOfField(fieldName, fieldValue).shouldBe("Amit");
		
		
		assertPreConditionOutput(fieldName, fieldValue,
				ValidationType.ShouldBe, Status.PreConditionNotMet,
				precoditionFieldName, preCoditionValue,PreConditionType.Is);
	}
	
	
	@Test
	public void testIsInAnyCasePreConditionNull() {
		
		String fieldName = "Name";
		String fieldValue = "Amit";
		String precoditionFieldName = "Age";
		String preCoditionValue = null;
		
		validator.ifValueOfField(precoditionFieldName, preCoditionValue).isInAnyCase("value1")
				.thenValueOfField(fieldName, fieldValue).shouldBe("Amit");
		
		
		assertPreConditionOutput(fieldName, fieldValue,
				ValidationType.ShouldBe, Status.PreConditionNotMet,
				precoditionFieldName, preCoditionValue,PreConditionType.Is);
	}
	
	@Test
	public void testIsNotInAnyCasePassed() {
		
		String fieldName = "Name";
		String fieldValue = "Amit";
		String precoditionFieldName = "Age";
		String preCoditionValue = "value";
		
		validator.ifValueOfField(precoditionFieldName, preCoditionValue).isNotInAnyCase("value2")
				.thenValueOfField(fieldName, fieldValue).shouldBe("Amit");
		
		
		assertPreConditionOutput(fieldName, fieldValue,
				ValidationType.ShouldBe, Status.Passed,
				precoditionFieldName, preCoditionValue,PreConditionType.Is);
	}
	
	@Test
	public void testIsNotInAnyCasePreConditionNotMet() {
		
		String fieldName = "Name";
		String fieldValue = "Amit";
		String precoditionFieldName = "Age";
		String preCoditionValue = "value";
		
		validator.ifValueOfField(precoditionFieldName, preCoditionValue).isNotInAnyCase("VALUE")
				.thenValueOfField(fieldName, fieldValue).shouldBe("Amit");
		
		
		assertPreConditionOutput(fieldName, fieldValue,
				ValidationType.ShouldBe, Status.PreConditionNotMet,
				precoditionFieldName, preCoditionValue,PreConditionType.Is);
	}
	
	
	@Test
	public void testIsNotInAnyCasePreConditionNull() {
		
		String fieldName = "Name";
		String fieldValue = "Amit";
		String precoditionFieldName = "Age";
		String preCoditionValue = null;
		
		validator.ifValueOfField(precoditionFieldName, preCoditionValue).isNotInAnyCase("value1")
				.thenValueOfField(fieldName, fieldValue).shouldBe("Amit");
		
		
		assertPreConditionOutput(fieldName, fieldValue,
				ValidationType.ShouldBe, Status.PreConditionNotMet,
				precoditionFieldName, preCoditionValue,PreConditionType.Is);
	}
	
	@Test
	public void testIsPreConditionNotMet() {
		
		String fieldName = "Name";
		String fieldValue = "Amit";
		String precoditionFieldName = "Age";
		int preCoditionValue = 30;
		
		validator.ifValueOfField(precoditionFieldName, preCoditionValue).is(29)
				.thenValueOfField(fieldName, fieldValue).shouldBe("Amit");
		
		
		assertPreConditionOutput(fieldName, fieldValue,
				ValidationType.ShouldBe, Status.PreConditionNotMet,
				precoditionFieldName, preCoditionValue,PreConditionType.Is);
	}
	
	@Test
	public void testIsPreConditionNotMetNull() {

		String fieldName = "Name";
		String fieldValue = "Amit";
		String precoditionFieldName = "Age";
		Integer preCoditionValue = null;

		validator.ifValueOfField(precoditionFieldName, preCoditionValue).is(29)
				.thenValueOfField(fieldName, fieldValue).shouldBe("Amit");

		assertPreConditionOutput(fieldName, fieldValue,
				ValidationType.ShouldBe, Status.PreConditionNotMet,
				precoditionFieldName, preCoditionValue,PreConditionType.Is);
	}
	
	
	@Test
	public void testIsNotPassed() {
		
		String fieldName = "Name";
		String fieldValue = "Amit";
		String precoditionFieldName = "Age";
		int preCoditionValue = 30;
		
		validator.ifValueOfField(precoditionFieldName, preCoditionValue).isNot(34)
				.thenValueOfField(fieldName, fieldValue).shouldBe("Amit");
		
		
		assertPreConditionOutput(fieldName, fieldValue,
				ValidationType.ShouldBe, Status.Passed,
				precoditionFieldName, preCoditionValue,PreConditionType.IsNot);
	}
	
	@Test
	public void testIsNotPreCondtitionNotMet() {
		
		String fieldName = "Name";
		String fieldValue = "Amit";
		String precoditionFieldName = "Age";
		int preCoditionValue = 30;
		
		validator.ifValueOfField(precoditionFieldName, preCoditionValue).isNot(30)
				.thenValueOfField(fieldName, fieldValue).shouldBe("Amit");
		
		
		assertPreConditionOutput(fieldName, fieldValue,
				ValidationType.ShouldBe, Status.PreConditionNotMet,
				precoditionFieldName, preCoditionValue,PreConditionType.IsNot);
	}
	
	@Test
	public void testIsNotPreCondtitionNotMetNull() {

		String fieldName = "Name";
		String fieldValue = "Amit";
		String precoditionFieldName = "Age";
		Integer preCoditionValue = null;

		validator.ifValueOfField(precoditionFieldName, preCoditionValue).isNot(29)
				.thenValueOfField(fieldName, fieldValue).shouldBe("Amit");

		assertPreConditionOutput(fieldName, fieldValue,
				ValidationType.ShouldBe, Status.PreConditionNotMet,
				precoditionFieldName, preCoditionValue,PreConditionType.IsNot);
	}
	
	@Test
	public void testIsNullPassed() {
		
		String fieldName = "Name";
		String fieldValue = "Amit";
		String precoditionFieldName = "Age";
		Integer preCoditionValue = null;
		
		validator.ifValueOfField(precoditionFieldName, preCoditionValue).isNull()
				.thenValueOfField(fieldName, fieldValue).shouldBe("Amit");
		
		
		assertPreConditionOutput(fieldName, fieldValue,
				ValidationType.ShouldBe, Status.Passed,
				precoditionFieldName, preCoditionValue,PreConditionType.IsNull);
	}
	
	@Test
	public void testIsNullPreCondtitionNotMet() {
		
		String fieldName = "Name";
		String fieldValue = "Amit";
		String precoditionFieldName = "Age";
		int preCoditionValue = 30;
		
		validator.ifValueOfField(precoditionFieldName, preCoditionValue).isNot(30)
				.thenValueOfField(fieldName, fieldValue).shouldBe("Amit");
		
		
		assertPreConditionOutput(fieldName, fieldValue,
				ValidationType.ShouldBe, Status.PreConditionNotMet,
				precoditionFieldName, preCoditionValue,PreConditionType.IsNot);
	}
	
	@Test
	public void testIsNotNullPassed() {
		
		String fieldName = "Name";
		String fieldValue = "Amit";
		String precoditionFieldName = "Age";
		Integer preCoditionValue = 56;
		
		validator.ifValueOfField(precoditionFieldName, preCoditionValue).isNotNull()
				.thenValueOfField(fieldName, fieldValue).shouldBe("Amit");
		
		
		assertPreConditionOutput(fieldName, fieldValue,
				ValidationType.ShouldBe, Status.Passed,
				precoditionFieldName, preCoditionValue,PreConditionType.IsNotNull);
	}
	
	@Test
	public void testIsnotNullPreCondtitionNotMet() {
		
		String fieldName = "Name";
		String fieldValue = "Amit";
		String precoditionFieldName = "Age";
		Integer preCoditionValue = null;
		
		validator.ifValueOfField(precoditionFieldName, preCoditionValue).isNotNull()
				.thenValueOfField(fieldName, fieldValue).shouldBe("Amit");
		
		
		assertPreConditionOutput(fieldName, fieldValue,
				ValidationType.ShouldBe, Status.PreConditionNotMet,
				precoditionFieldName, preCoditionValue,PreConditionType.IsNotNull);
	}
	
	
	@Test
	public void testIsBlankPassed() {
		
		String fieldName = "Name";
		String fieldValue = "Amit";
		String precoditionFieldName = "Age";
		Integer preCoditionValue = null;
		
		validator.ifValueOfField(precoditionFieldName, preCoditionValue).isBlank()
				.thenValueOfField(fieldName, fieldValue).shouldBe("Amit");
		
		
		assertPreConditionOutput(fieldName, fieldValue,
				ValidationType.ShouldBe, Status.Passed,
				precoditionFieldName, preCoditionValue,PreConditionType.IsBlank);
	}
	
	@Test
	public void testIsBlankPreCondtitionNotMet() {
		
		String fieldName = "Name";
		String fieldValue = "Amit";
		String precoditionFieldName = "Age";
		Integer preCoditionValue = 67;
		
		validator.ifValueOfField(precoditionFieldName, preCoditionValue).isBlank()
				.thenValueOfField(fieldName, fieldValue).shouldBe("Amit");
		
		
		assertPreConditionOutput(fieldName, fieldValue,
				ValidationType.ShouldBe, Status.PreConditionNotMet,
				precoditionFieldName, preCoditionValue,PreConditionType.IsBlank);
	}
	
	@Test
	public void testIsNotBlankPassed() {
		
		String fieldName = "Name";
		String fieldValue = "Amit";
		String precoditionFieldName = "Age";
		Integer preCoditionValue = 45;
		
		validator.ifValueOfField(precoditionFieldName, preCoditionValue).isNotBlank()
				.thenValueOfField(fieldName, fieldValue).shouldBe("Amit");
		
		
		assertPreConditionOutput(fieldName, fieldValue,
				ValidationType.ShouldBe, Status.Passed,
				precoditionFieldName, preCoditionValue,PreConditionType.IsNotBlank);
	}
	
	@Test
	public void testIsNotBlankPreCondtitionNotMet() {
		
		String fieldName = "Name";
		String fieldValue = "Amit";
		String precoditionFieldName = "Age";
		Integer preCoditionValue = null;
		
		validator.ifValueOfField(precoditionFieldName, preCoditionValue).isNotBlank()
				.thenValueOfField(fieldName, fieldValue).shouldBe("Amit");
		
		
		assertPreConditionOutput(fieldName, fieldValue,
				ValidationType.ShouldBe, Status.PreConditionNotMet,
				precoditionFieldName, preCoditionValue,PreConditionType.IsNotBlank);
	}
	
	
	@Test
	public void testIsNumericPreCondtitionNotMet() {
		
		String fieldName = "Name";
		String fieldValue = "Amit";
		String precoditionFieldName = "Age";
		String preCoditionValue = "alpha";
		
		validator.ifValueOfField(precoditionFieldName, preCoditionValue).isNumeric()
				.thenValueOfField(fieldName, fieldValue).shouldBe("Amit");
		
		
		assertPreConditionOutput(fieldName, fieldValue,
				ValidationType.ShouldBe, Status.PreConditionNotMet,
				precoditionFieldName, preCoditionValue,PreConditionType.IsNumeric);
	}
	
	@Test
	public void testIsNumericpreConditionNotMetNullValue() {
		
		String fieldName = "Name";
		String fieldValue = "Amit";
		String precoditionFieldName = "Age";
		String preCoditionValue = null;
		
		validator.ifValueOfField(precoditionFieldName, preCoditionValue).isNumeric()
				.thenValueOfField(fieldName, fieldValue).shouldBe("Amit");
		
		
		assertPreConditionOutput(fieldName, fieldValue,
				ValidationType.ShouldBe, Status.PreConditionNotMet,
				precoditionFieldName, preCoditionValue,PreConditionType.IsNumeric);
	}
	
	
	@Test
	public void testIsNumericPassed() {
		
		String fieldName = "Name";
		String fieldValue = "Amit";
		String precoditionFieldName = "Age";
		String preCoditionValue = "45";
		
		validator.ifValueOfField(precoditionFieldName, preCoditionValue).isNumeric()
				.thenValueOfField(fieldName, fieldValue).shouldBe("Amit");
		
		
		assertPreConditionOutput(fieldName, fieldValue,
				ValidationType.ShouldBe, Status.Passed,
				precoditionFieldName, preCoditionValue,PreConditionType.IsNumeric);
	}
	
	
	
	private void assertPreConditionOutput(String fieldName, String fieldValue,
			ValidationType validationType, Status expectedStatus,
			String precoditionFieldName, Object preCoditionValue,PreConditionType preConditionType) {

		assertOutput(fieldName, fieldValue, expectedStatus, validationType);

		PreResult preResults = validationListener.getResult().getCondition()
				.getPreResult();

		Assert.assertNotNull(preResults);

		Assert.assertEquals(precoditionFieldName, preResults.getPreCondition()
				.getFieldName());
		Assert.assertEquals(preCoditionValue, preResults.getPreCondition()
				.getFieldValue());
		Assert.assertEquals(preConditionType, preResults.getType());
		
	}

	private void assertOneOfOutput(String fieldName, Object fieldValue,
			List<Object> domainValues, Status status,
			ValidationType type) {
		
		assertOutput(fieldName, fieldValue, status, type);

		Assert.assertTrue(domainValues.containsAll(validationListener.getResult().getDomainValues()));
		Assert.assertTrue((validationListener.getResult().getDomainValues()).containsAll(domainValues));
		
	}

	private void assertSyntaxOutput(String name, Object value, Status status,
			ValidationType type,String syntax) {
		assertOutput(name, value, status, type);
		Assert.assertEquals(syntax, validationListener.getResult().getSyntax());
	}
	
	
	/**
	 * @param name
	 * @param value
	 * @param status
	 * @param type
	 */
	private void assertOutput(String name, Object value, Status status,
			ValidationType type) {
		Assert.assertNotNull(validationListener.getResult());
		Assert.assertEquals(name, validationListener.getResult().getFieldName());
		Assert.assertEquals(value, validationListener.getResult()
				.getFieldValue());
		Assert.assertEquals(status, validationListener.getResult().getStatus());
		Assert.assertEquals(type, validationListener.getResult().getType());
	}

	/**
	 * @param name
	 * @param value
	 * @param status
	 * @param type
	 */
	private void assertCompareOutput(String fieldName, Object fieldValue,
			Object comparedValue, Status status, ValidationType type) {
		assertOutput(fieldName, fieldValue, status, type);

		Assert.assertEquals(comparedValue, validationListener.getResult()
				.getComparedValue());

		
	}

	/**
	 * @param name
	 * @param value
	 * @param status
	 * @param type
	 */
	private void assertRangeOutput(String fieldName, Object fieldValue,
			Object fromRange, Object toRange, Status status, ValidationType type) {
		
		assertOutput(fieldName, fieldValue, status, type);

		Assert.assertEquals(fromRange, validationListener.getResult()
				.getFromRange());
		Assert.assertEquals(toRange, validationListener.getResult()
				.getToRange());

		
	}

}
