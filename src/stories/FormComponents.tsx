import React, { DetailedHTMLProps, InputHTMLAttributes } from "react";
import { DateSelectArg } from "@fullcalendar/react";
import { useForm } from "react-hook-form";

interface TextInputProps {
  label: string;
}

type TextInputDefinition = TextInputProps &
  DetailedHTMLProps<InputHTMLAttributes<HTMLInputElement>, HTMLInputElement>;

type InputProps = TextInputDefinition;

export const FormInput = ({ label, ...props }: InputProps) => (
  <div className="mt-2">
    <p className="text-sm text-gray-500">{label}</p>
    <input
      {...props}
      type={props.type || "text"}
      className="form-input block w-full"
    />
  </div>
);
