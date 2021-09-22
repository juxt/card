import { CalendarProps, EventCalendar } from "./Calendar";
import { Meta, Story } from "@storybook/react";
import { MOCK_EVENTS, MOCK_PROJECTS } from "../utils";

export default {
  title: "People/Calendar",
  component: EventCalendar,
  parameters: { actions: { argTypesRegex: "^on.*" } },
} as Meta;

const Template: Story<CalendarProps> = (args) => <EventCalendar {...args} />;
export const Calendar = Template.bind({});
Calendar.args = {
  isCurrentUser: true,
  events: MOCK_EVENTS,
  projectOptions: MOCK_PROJECTS,
};

export const CalendarNoEvents = Template.bind({});
CalendarNoEvents.args = {
  isCurrentUser: true,
  events: [],
};
export const CalendarNotCurrentUser = Template.bind({});
CalendarNotCurrentUser.args = {
  events: MOCK_EVENTS,
};
