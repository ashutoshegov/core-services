const { assign, actions } = require('xstate');
const dialog = require('./util/dialog');
const messages = require('./messages/covid-info');

const covidInfoFlow = {
  id: 'covidInfoFlow',
  initial: 'covidInfoMenu',
  states: {
    covidInfoMenu: {
      id: 'covidInfoMenu',
      initial: 'prompt',
      states: {
        prompt: {
          onEntry: assign((context, event) => {
            let message = dialog.get_message(messages.covidInfoMenu.prompt.preamble, context.user.locale);
            let { grammer, prompt } = dialog.constructListPromptAndGrammer(messages.covidInfoMenu.prompt.options.list, messages.covidInfoMenu.prompt.options.messageBundle, context.user.locale);
            message += prompt;
            context.grammer = grammer;
            dialog.sendMessage(context, message);
          }),
          on: {
            USER_MESSAGE: 'process'
          }
        },
        process: {
          onEntry: assign((context, event) => {
            context.intention = dialog.get_intention(context.grammer, event, true);
          }),
          always: [
            {
              cond: (context) => context.intention == 'selfCare',
              target: '#selfCareInfo'
            },
            {
              cond: (context) => context.intention == 'fatehKit',
              target: '#fatehKitInfo'
            },
            {
              cond: (context) => context.intention == 'bedAvailability',
              target: '#bedAvailabilityInfo'
            },
            {
              cond: (context) => context.intention == 'vaccinationCenters',
              target: '#vaccinationCentersInfo'
            },
            {
              target: 'error'
            },
          ]
        },
        error: {
          onEntry: assign((context, event) => {
            dialog.sendMessage(context, dialog.get_message(dialog.global_messages.error.optionsRetry, context.user.locale), false);
          }),
          always: 'prompt'
        }
      }
    },
    selfCareInfo: {
      id: 'selfCareInfo',
      onEntry: assign((context, event) => {
        dialog.sendMessage(context, dialog.get_message(messages.selfCareInfo, context.user.locale));
      }),
      always: '#endstate'
    },
    fatehKitInfo: {
      id: 'fatehKitInfo',
      onEntry: assign((context, event) => {
        dialog.sendMessage(context, dialog.get_message(messages.fatehKitInfo, context.user.locale));
      }),
      always: '#endstate'
    },
    bedAvailabilityInfo: {
      id: 'bedAvailabilityInfo',
      onEntry: assign((context, event) => {
        dialog.sendMessage(context, dialog.get_message(messages.fatehKitInfo, context.user.locale));
      }),
      always: '#endstate'
    },
    vaccinationCentersInfo: {
      id: 'vaccinationCentersInfo',
      onEntry: assign((context, event) => {
        dialog.sendMessage(context, dialog.get_message(messages.fatehKitInfo, context.user.locale));
      }),
      always: '#endstate'
    }
  }
};

module.exports = covidInfoFlow;
