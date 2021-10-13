import { CalendarProps, EventCalendar } from "./Calendar";
import { Meta, Story } from "@storybook/react";
import { MOCK_EVENTS, MOCK_PROJECTS } from "../utils";

export default {
  title: "People/Calendar",
  component: EventCalendar,
  parameters: { actions: { argTypesRegex: "^on.*" } },
} as Meta;

const Template: Story<CalendarProps> = (args) => <EventCalendar {...args} />;
const defaultArgs = {
  events: MOCK_EVENTS,
  isCurrentUser: true,
  projectOptions: MOCK_PROJECTS,
};

export const Calendar = Template.bind({});
Calendar.args = defaultArgs;

export const CalendarAlwaysTimeSheet = Template.bind({});
CalendarAlwaysTimeSheet.args = {
  ...defaultArgs,
  eventType: "Timesheet",
};

export const CalendarAlwaysHoliday = Template.bind({});
CalendarAlwaysHoliday.args = {
  ...defaultArgs,
  eventType: "Holiday",
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
