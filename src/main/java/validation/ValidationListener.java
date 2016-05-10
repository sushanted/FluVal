package validation;

import validation.Validator.Result;


public interface ValidationListener {
	public void validated(Result<?> result);
}
