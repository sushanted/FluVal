package validation;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Validator {

	private ValidationListener validationListener;

	public static enum ValidationType {

		ShouldBe, ShouldNotBe, ShouldBeNull, ShouldBeBlank, ShouldNotBeBlank, ShouldNotBeNull, ShouldBeNumeric, ShouldBeBoolean, ShouldBeGreaterThan, ShouldBeLessThan, ShouldBeInInclusiveRange, ShouldBeInExclusiveRange, ShouldBeOneOf, ShouldMatch, ShouldBeGreaterThanOtherField, ShouldBeLessThanOtherField, ShouldBeAnExistingFile
	}

	public static enum PreConditionType {
		Is, IsNot, IsNull, IsBlank, IsNotNull, IsNotBlank, IsNumeric, IsBoolean, IsGreaterThan, IsLessThan, IsInInclusiveRange, IsInExclusiveRange, IsOneOf, IsAnExistingFile
	}

	public static enum Status {
		Failed, Passed, NullValueOperation, IncompatibleType, PreConditionNotMet
	}

	// TODO there are common things with Result and PreResult, can take out a
	// common class
	public static class PreResult<T> {

		private PreConditionType type;

		private Status status;

		private PreCondition<T> preCondition;

		private T fromRange;
		private T toRange;

		private String comparedFieldName;
		private T comparedValue;

		public PreResult(PreConditionType type, PreCondition<T> preCondition,
				Status status) {
			this.type = type;
			this.preCondition = preCondition;
			this.status = status;
		}

		public PreConditionType getType() {
			return type;
		}

		public PreResult<T> setType(PreConditionType type) {
			this.type = type;
			return this;
		}

		public Status getStatus() {
			return status;
		}

		public PreResult<T> setStatus(Status status) {
			this.status = status;
			return this;
		}

		public PreCondition<T> getPreCondition() {
			return preCondition;
		}

		public PreResult<T> setPreCondition(PreCondition<T> preCondition) {
			this.preCondition = preCondition;
			return this;
		}

		public T getFromRange() {
			return fromRange;
		}

		public PreResult<T> setFromRange(T fromRange) {
			this.fromRange = fromRange;
			return this;
		}

		public T getToRange() {
			return toRange;
		}

		public PreResult<T> setToRange(T toRange) {
			this.toRange = toRange;
			return this;
		}

		public String getComparedFieldName() {
			return comparedFieldName;
		}

		public PreResult<T> setComparedFieldName(String comparedFieldName) {
			this.comparedFieldName = comparedFieldName;
			return this;
		}

		public T getComparedValue() {
			return comparedValue;
		}

		public PreResult<T> setComparedValue(T comparedValue) {
			this.comparedValue = comparedValue;
			return this;
		}

	}

	public static class Result<T> {

		private ValidationType type;

		private Status status;

		private Condition<T> condition;

		private T fromRange;
		private T toRange;

		private String comparedFieldName;
		private T comparedValue;
		private Object comparedFieldOriginalValue;

		private Collection<T> domainValues;

		private String syntax;

		public Result(ValidationType type, Condition<T> condition, Status status) {
			this.type = type;
			this.condition = condition;
			this.status = status;
		}

		public ValidationType getType() {
			return type;
		}

		public void setType(ValidationType type) {
			this.type = type;
		}

		public Status getStatus() {
			return status;
		}

		public void setStatus(Status status) {
			this.status = status;
		}

		public Condition<T> getCondition() {
			return condition;
		}

		public void setCondition(Condition<T> condition) {
			this.condition = condition;
		}

		public String getFieldName() {
			return condition.getFieldName();
		}

		public T getFieldValue() {
			return condition.getFieldValue();
		}

		public Object getPresentationValue() {
			return condition.getOriginalValue() != null ? condition
					.getOriginalValue() : condition.getFieldValue();
		}

		public T getFromRange() {
			return fromRange;
		}

		public Result<T> setFromRange(T fromRange) {
			this.fromRange = fromRange;
			return this;
		}

		public T getToRange() {
			return toRange;
		}

		public Result<T> setToRange(T toRange) {
			this.toRange = toRange;
			return this;
		}

		public String getComparedFieldName() {
			return comparedFieldName;
		}

		public Result<T> setComparedFieldName(String comparedFieldName) {
			this.comparedFieldName = comparedFieldName;
			return this;
		}

		public T getComparedValue() {
			return comparedValue;
		}

		public Result<T> setComparedValue(T comparedValue) {
			this.comparedValue = comparedValue;
			return this;
		}

		public Collection<T> getDomainValues() {
			return domainValues;
		}

		public Result<T> setDomainValues(Collection<T> domainValues) {
			this.domainValues = domainValues;
			return this;
		}

		public String getSyntax() {
			return syntax;
		}

		public Result<T> setSyntax(String syntax) {
			this.syntax = syntax;
			return this;
		}

		public Object getComparedFieldPresentationValue() {
			return comparedFieldOriginalValue != null ? comparedFieldOriginalValue
					: comparedValue;
		}

		public Result<T> setComparedFieldOriginalValue(
				Object comparedFieldOriginalValue) {
			this.comparedFieldOriginalValue = comparedFieldOriginalValue;
			return this;
		}

	}

	public class PreCondition<T> {

		private String fieldName;
		private T fieldValue;

		private PreResult<T> result;

		public PreCondition(String fieldName, T value) {
			this.fieldName = fieldName;
			this.fieldValue = value;
		}

		public String getFieldName() {
			return fieldName;
		}

		public T getFieldValue() {
			return fieldValue;
		}

		public PreCondition<T> is(T value) {

			Status status = getStatus(() -> fieldValue.equals(value) ? Status.Passed
					: Status.Failed);

			this.result = new PreResult<T>(PreConditionType.Is, this, status);

			return this;
		}
		
		public PreCondition<T> isInAnyCase(String value) {

			Status status = getStatus(() -> ((String) fieldValue)
					.equalsIgnoreCase(value) ? Status.Passed : Status.Failed);

			this.result = new PreResult<T>(PreConditionType.Is, this, status);

			return this;
		}

		public PreCondition<T> isNot(T value) {

			Status status = getStatus(() -> (!fieldValue.equals(value)) ? Status.Passed
					: Status.Failed);

			this.result = new PreResult<T>(PreConditionType.IsNot, this, status);

			return this;
		}
		
		
		public PreCondition<T> isNotInAnyCase(String value) {

			Status status = getStatus(() -> ((String) fieldValue)
					.equalsIgnoreCase(value) ? Status.Failed : Status.Passed);

			this.result = new PreResult<T>(PreConditionType.Is, this, status);

			return this;
		}

		public PreCondition<T> isNull() {

			Status status = (fieldValue == null) ? Status.Passed
					: Status.Failed;

			this.result = new PreResult<T>(PreConditionType.IsNull, this,
					status);

			return this;
		}

		public PreCondition<T> isNotNull() {

			Status status = (fieldValue != null) ? Status.Passed
					: Status.Failed;

			this.result = new PreResult<T>(PreConditionType.IsNotNull, this,
					status);

			return this;
		}

		public PreCondition<T> isBlank() {

			Status status = isBlankValue(fieldValue) ? Status.Passed
					: Status.Failed;

			this.result = new PreResult<T>(PreConditionType.IsBlank, this,
					status);

			return this;
		}

		public PreCondition<T> isNotBlank() {

			Status status = (!isBlankValue(fieldValue)) ? Status.Passed
					: Status.Failed;

			this.result = new PreResult<T>(PreConditionType.IsNotBlank, this,
					status);

			return this;
		}

		public PreCondition<T> isNumeric() {

			Status status = (isValueNumeric(fieldValue)) ? Status.Passed
					: Status.Failed;

			this.result = new PreResult<T>(PreConditionType.IsNumeric, this,
					status);

			return this;
		}

		public PreCondition<T> isBoolean() {

			Status status = (isValueBoolean(fieldValue)) ? Status.Passed
					: Status.Failed;

			this.result = new PreResult<T>(PreConditionType.IsBoolean, this,
					status);

			return this;
		}

		public PreCondition<T> isGreaterThan(T value) {

			Status status = getComparableStatus(() -> {
				return (((Comparable<T>) fieldValue).compareTo(value) <= 0) ? Status.Failed
						: Status.Passed;
			});

			this.result = new PreResult<T>(PreConditionType.IsGreaterThan,
					this, status);

			return this;
		}

		public PreCondition<T> isLesserThan(T value) {

			Status status = getComparableStatus(() -> {
				return (((Comparable<T>) fieldValue).compareTo(value) >= 0) ? Status.Failed
						: Status.Passed;
			});

			this.result = new PreResult<T>(PreConditionType.IsGreaterThan,
					this, status);

			return this;
		}

		public PreCondition<T> isAnExistingFile() {

			Status status = getStatus(() -> {
				return new File((String) fieldValue).exists() ? Status.Passed
						: Status.Failed;
			});

			this.result = new PreResult<T>(PreConditionType.IsAnExistingFile,
					this, status);

			return this;
		}

		public <K, M> Condition<M> thenValueOfField(String fieldName,
				K fieldValue, Function<K, M> transformer) {

			M transformedValue = null;
			if (fieldValue != null) {
				try {
					transformedValue = transformer.apply(fieldValue);
				} catch (Exception e) {
					// TODO This should be put in the condition
				}
			}
			return new Condition<M>(fieldName, transformedValue, this.result);
		}

		public <K> Condition<K> thenValueOfField(String fieldName, K fieldValue) {
			return new Condition<K>(fieldName, fieldValue, this.result);
		}

		// TODO copied from condition : refactor : avoid duplication
		public Status getComparableStatus(Supplier<Status> statusSupplier) {

			Supplier<Status> comparableSupplier = () -> {

				if (!(fieldValue instanceof Comparable)) {
					return Status.IncompatibleType;
				} else {
					return statusSupplier.get();
				}

			};

			return getStatus(comparableSupplier);
		}

		public Status getStatus(Supplier<Status> statusSupplier) {
			return getStatus(statusSupplier, true);
		}

		public Status getStatus(Supplier<Status> statusSupplier,
				boolean checkNull) {

			if (checkNull && fieldValue == null) {
				return Status.NullValueOperation;
			} else {
				return statusSupplier.get();
			}

		}

	}

	public class Condition<T> {

		private String fieldName;
		private T fieldValue;

		private Object originalValue;

		private PreResult preResult;

		public PreResult getPreResult() {
			return preResult;
		}

		public Condition<T> setPreResult(PreResult preResult) {
			this.preResult = preResult;
			return this;
		}

		public String getFieldName() {
			return fieldName;
		}

		public void setFieldName(String fieldName) {
			this.fieldName = fieldName;
		}

		public T getFieldValue() {
			return fieldValue;
		}

		public void setFieldValue(T fieldValue) {
			this.fieldValue = fieldValue;
		}

		public Condition(String fieldName, T fieldValue) {
			this.fieldName = fieldName;
			this.fieldValue = fieldValue;
		}

		public <K> Condition(String fieldName, T fieldValue,
				PreResult<K> preResult) {
			this(fieldName, fieldValue);
			this.preResult = preResult;
		}

		public Object getOriginalValue() {
			return originalValue;
		}

		public Condition<T> setOriginalValue(Object originalValue) {
			this.originalValue = originalValue;
			return this;
		}

		public Condition<T> shouldBeNull() {

			validationListener.validated(new Result<T>(
					ValidationType.ShouldBeNull, this,
					fieldValue == null ? Status.Passed : Status.Failed));

			return this;
		}

		public Condition<T> shouldNotBeNull() {

			validationListener.validated(new Result<T>(
					ValidationType.ShouldNotBeNull, this,
					fieldValue != null ? Status.Passed : Status.Failed));

			return this;
		}

		public Condition<T> shouldBeBlank() {

			Status status = getStatus(
					() -> isBlankValue(fieldValue) ? Status.Passed
							: Status.Failed, false);

			validationListener.validated(new Result<T>(
					ValidationType.ShouldBeBlank, this, status));

			return this;
		}

		public Condition<T> shouldNotBeBlank() {

			Status status = getStatus(
					() -> isBlankValue(fieldValue) ? Status.Failed
							: Status.Passed, false);

			validationListener.validated(new Result<T>(
					ValidationType.ShouldNotBeBlank, this, status));

			return this;
		}

		public Condition<T> shouldBe(T value) {

			Status status = getStatus(() -> fieldValue.equals(value) ? Status.Passed
					: Status.Failed);

			validationListener.validated(new Result<T>(ValidationType.ShouldBe,
					this, status));

			return this;
		}

		public Condition<T> shouldNotBe(T value) {

			Status status = getStatus(() -> !fieldValue.equals(value) ? Status.Passed
					: Status.Failed);

			validationListener.validated(new Result<T>(ValidationType.ShouldBe,
					this, status));

			return this;
		}

		public Condition<T> shouldBeNumeric() {

			Status status = getStatus(() -> isValueNumeric(fieldValue) ? Status.Passed
					: Status.Failed);

			validationListener.validated(new Result<T>(
					ValidationType.ShouldBeNumeric, this, status));

			return this;
		}

		public Condition<T> shouldBeBoolean() {

			Status status = getStatus(() -> isValueBoolean(fieldValue) ? Status.Passed
					: Status.Failed);

			validationListener.validated(new Result<T>(
					ValidationType.ShouldBeBoolean, this, status));

			return this;
		}

		public Condition<T> shouldBeGreaterThan(T value) {

			Status status = getComparableStatus(() -> {
				return (((Comparable<T>) fieldValue).compareTo(value) <= 0) ? Status.Failed
						: Status.Passed;
			});

			validationListener.validated(new Result<T>(
					ValidationType.ShouldBeGreaterThan, this, status)
					.setComparedValue((T) value));

			return this;

		}

		// TODO try this out, better
		/*
		 * public <K extends Comparable<T>> Condition<T> shouldBeGreaterThan(K
		 * value) {
		 * 
		 * Status status = getComparableStatus(() -> { return
		 * ((value.compareTo(fieldValue)) <= 0) ? Status.Failed : Status.Passed;
		 * });
		 * 
		 * validationListener.validated(new Result<T>(
		 * ValidationType.ShouldBeGreaterThan, this, status)
		 * .setComparedValue((T)value));
		 * 
		 * return this;
		 * 
		 * }
		 */

		public Condition<T> shouldBeLessThan(T value) {
			Status status = getComparableStatus(() -> {
				return (((Comparable<T>) fieldValue).compareTo(value) >= 0) ? Status.Failed
						: Status.Passed;
			});

			validationListener.validated(new Result<T>(
					ValidationType.ShouldBeLessThan, this, status)
					.setComparedValue(value));

			return this;
		}

		public Condition<T> shouldBeGreaterThanField(String fieldName, T value) {

			Status status = getComparableStatus(() -> {
				return (((Comparable<T>) fieldValue).compareTo(value) <= 0) ? Status.Failed
						: Status.Passed;
			});

			validationListener.validated(new Result<T>(
					ValidationType.ShouldBeGreaterThanOtherField, this, status)
					.setComparedValue((T) value)
					.setComparedFieldName(fieldName));

			return this;

		}

		public Condition<T> shouldBeGreaterThanField(String fieldName, T value,
				Object originalValue) {

			Status status = getComparableStatus(() -> {
				return (((Comparable<T>) fieldValue).compareTo(value) <= 0) ? Status.Failed
						: Status.Passed;
			});

			validationListener.validated(new Result<T>(
					ValidationType.ShouldBeGreaterThanOtherField, this, status)
					.setComparedValue((T) value)
					.setComparedFieldName(fieldName)
					.setComparedFieldOriginalValue(originalValue));

			return this;

		}

		public Condition<T> shouldBeLessThanField(String fieldName, T value) {

			Status status = getComparableStatus(() -> {
				return (((Comparable<T>) fieldValue).compareTo(value) >= 0) ? Status.Failed
						: Status.Passed;
			});

			validationListener.validated(new Result<T>(
					ValidationType.ShouldBeLessThanOtherField, this, status)
					.setComparedValue((T) value)
					.setComparedFieldName(fieldName));

			return this;

		}

		public Condition<T> shouldBeLessThanField(String fieldName, T value,
				Object originalValue) {

			Status status = getComparableStatus(() -> {
				return (((Comparable<T>) fieldValue).compareTo(value) >= 0) ? Status.Failed
						: Status.Passed;
			});

			validationListener.validated(new Result<T>(
					ValidationType.ShouldBeLessThanOtherField, this, status)
					.setComparedValue((T) value)
					.setComparedFieldName(fieldName)
					.setComparedFieldOriginalValue(originalValue));

			return this;

		}

		public Condition<T> shouldBeInInclusiveRange(T from, T to) {

			Status status = getComparableStatus(() -> {
				return (((Comparable<T>) fieldValue).compareTo(from) < 0 || ((Comparable<T>) fieldValue)
						.compareTo(to) > 0) ? Status.Failed : Status.Passed;
			});

			validationListener.validated(new Result<T>(
					ValidationType.ShouldBeInInclusiveRange, this, status)
					.setFromRange(from).setToRange(to));

			return this;
		}

		public Condition<T> shouldBeInExclusiveRange(T from, T to) {

			Status status = getComparableStatus(() -> {
				return (((Comparable<T>) fieldValue).compareTo(from) <= 0 || ((Comparable<T>) fieldValue)
						.compareTo(to) >= 0) ? Status.Failed : Status.Passed;
			});

			validationListener.validated(new Result<T>(
					ValidationType.ShouldBeInExclusiveRange, this, status)
					.setFromRange(from).setToRange(to));

			return this;
		}

		public Condition<T> shouldBeOneOfEnumNames(Class<?> enumClass) {

			List<String> values = getEnumNamesCollection(enumClass);

			return shouldBeOneOf((Collection<T>) values);
		}

		public Condition<T> shouldBeOneOfEnumNamesInAnyCase(Class<?> enumClass) {

			List<String> values = getEnumNamesCollection(enumClass);

			return shouldBeOneOfInAnyCase((Collection<T>) values);
		}

		public Condition<T> shouldMatch(Pattern pattern, String syntax) {

			Status status = getStatus(() -> pattern.matcher(
					String.valueOf(fieldValue)).matches() ? Status.Passed
					: Status.Failed);

			validationListener
					.validated(new Result<T>(ValidationType.ShouldMatch, this,
							status).setSyntax(syntax));

			return this;
		}

		public Condition<T> shouldBeAnExistingFile() {

			Status status = getStatus(() -> new File((String) fieldValue)
					.exists() ? Status.Passed : Status.Failed);

			validationListener.validated(new Result<T>(
					ValidationType.ShouldBeAnExistingFile, this, status));

			return this;
		}

		/**
		 * @param enumClass
		 * @return
		 */
		private List<String> getEnumNamesCollection(Class<?> enumClass) {
			EnumSet enumSet = (EnumSet.allOf((Class<Enum>) enumClass));

			List<String> values = (List<String>) enumSet.stream()
					.map(value -> String.valueOf(value))
					.collect(Collectors.toList());
			return values;
		}

		public Condition<T> shouldBeOneOf(T... values) {

			return shouldBeOneOf(Arrays.asList(values));
		}

		public Condition<T> shouldBeOneOfInAnyCase(T... values) {

			return shouldBeOneOfInAnyCase(Arrays.asList(values));
		}

		public Condition<T> shouldBeOneOf(Collection<T> values) {

			Status status = getStatus(() -> {
				return (values.contains(fieldValue)) ? Status.Passed
						: Status.Failed;
			});

			validationListener.validated(new Result<T>(
					ValidationType.ShouldBeOneOf, this, status)
					.setDomainValues(values));

			return this;
		}

		public Condition<T> shouldBeOneOfInAnyCase(Collection<T> values) {

			Collection<String> stringValues = values.stream()
					.map(value -> value.toString().toUpperCase())
					.collect(Collectors.toCollection(HashSet::new));

			Status status = getStatus(() -> {
				return (stringValues.contains(fieldValue.toString()
						.toUpperCase())) ? Status.Passed : Status.Failed;
			});

			validationListener.validated(new Result<T>(
					ValidationType.ShouldBeOneOf, this, status)
					.setDomainValues(values));

			return this;
		}

		public Condition<T> and() {
			return this;
		}

		public Status getComparableStatus(Supplier<Status> statusSupplier) {

			Supplier<Status> comparableSupplier = () -> {

				if (!(fieldValue instanceof Comparable)) {
					return Status.IncompatibleType;
				} else {
					return statusSupplier.get();
				}

			};

			return getStatus(comparableSupplier);
		}

		public Status getStatus(Supplier<Status> statusSupplier) {
			return getStatus(statusSupplier, true);
		}

		public Status getStatus(Supplier<Status> statusSupplier,
				boolean checkNull) {

			Status status = checkPreResult();

			if (status == null) {

				if (checkNull
						&& (fieldValue == null || (fieldValue instanceof String)
								&& ((String) fieldValue).isEmpty())) {
					status = Status.NullValueOperation;
				} else {
					status = statusSupplier.get();
				}
			}

			return status;
		}

		/**
		 * @return
		 */
		private Status checkPreResult() {
			Status status = null;

			if (preResult != null
					&& (preResult.getStatus() == Status.Failed || preResult
							.getStatus() == Status.NullValueOperation)) {
				status = Status.PreConditionNotMet;
			}
			return status;
		}

	}

	public <T> PreCondition<T> ifValueOfField(String fieldName, T value) {
		return new PreCondition<T>(fieldName, value);
	}

	public <T> Condition<T> valueOfField(String fieldName, T value) {
		return new Condition<T>(fieldName, value);
	}

	public <T> Condition<T> valueOfField(String fieldName, T value,
			Object originalValue) {
		return new Condition<T>(fieldName, value)
				.setOriginalValue(originalValue);
	}

	public <T> Validator mandateField(String fieldName, T value) {
		new Condition<T>(fieldName, value).shouldNotBeBlank();
		return this;
	}

	public ValidationListener getValidationListener() {
		return validationListener;
	}

	public void setValidationListener(ValidationListener validationListener) {
		this.validationListener = validationListener;
	}

	/**
	 * @return
	 */
	public <V> boolean isBlankValue(V value) {
		boolean isBlank = false;

		if (value == null) {
			isBlank = true;
		} else if ((value instanceof String)
				&& ((String) value).trim().isEmpty()) {
			isBlank = true;
		}
		return isBlank;
	}

	public <V> boolean isValueNumeric(V value) {
		if (value instanceof Integer || value instanceof Long) {
			return true;
		}
		return (value instanceof String)
				&& ((String) value).matches("[+-]?\\d+");
	}

	public <V> boolean isValueBoolean(V value) {
		if (value instanceof Boolean) {
			return true;
		}
		return (value instanceof String)
				&& ((String) value).matches("(?i)true|false");
	}

}
