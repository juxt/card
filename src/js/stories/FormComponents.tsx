import React, { FC } from "react";
import cx from "classnames";
import { ValidationRule, UseFormProps } from "react-hook-form";

type InputProps = React.DetailedHTMLProps<
  React.InputHTMLAttributes<HTMLInputElement>,
  HTMLInputElement
>;

const Input: FC<InputProps> = ({
  name,
  type,
  label,
  rules = {},
  register,
  errors = {},
  ...rest
}) => {
  return (
    <div className="input-block">
      <label htmlFor={name}>{label}</label>
      <br />
      <input
        className={cx("input", errors[name] && "is-danger")}
        aria-invalid={errors[name] ? "true" : "false"}
        type={type}
        name={name}
        id={name}
        ref={register && register(rules)}
        {...rest}
      />
      <br />
      {errors[name] && (
        <p className="help is-danger" role="alert">
          {errors[name].message}
        </p>
      )}
    </div>
  );
};

export default Input;

