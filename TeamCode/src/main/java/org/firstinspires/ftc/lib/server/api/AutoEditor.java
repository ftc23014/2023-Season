package org.firstinspires.ftc.lib.server.api;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.checkerframework.checker.regex.qual.Regex;
import org.firstinspires.ftc.lib.CustomLambda;
import org.firstinspires.ftc.lib.systems.commands.*;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

public class AutoEditor {
    enum VariableType {
        INTEGER,
        DOUBLE,
        BOOLEAN,
        STRING,
        UNKNOWN;
    }

    public class AutoVariable {
        private String name;
        private VariableType type;
        private Object value;

        public AutoVariable(String name, VariableType type, Object value) {
            this.name = name;
            this.type = type;
            this.value = value;
        }

        public VariableType getType() {
            return this.type;
        }

        public String getName() {
            return name;
        }

        public void updateValue(Object newValue) {
            this.value = newValue;
        }

        public int getAsInt() {
            return (int) value;
        }

        public double getAsDouble() {
            return (double) value;
        }

        public boolean getAsBoolean() {
            return (boolean) value;
        }

        public String getAsString() {
            return (String) value;
        }

        @Override
        public String toString() {
            return value.toString();
        }
    }

    public class AutoProgram {
        private ArrayList<Command> commands = new ArrayList<>();
        private ArrayList<AutoVariable> globalVariables = new ArrayList<>();

        public AutoProgram() {
            if (instantiableCommands == null) {
                registerCustomCommands();
            }
        }

        private VariableType testStringForType(String t) {
            if (t.startsWith("\"") && t.endsWith("\"")) {
                return VariableType.STRING;
            } else if (t.contains(".") && t.split("\\.").length == 2) {
                if (t.split("\\.")[0].matches("/^\\d+$/") && t.split("\\.")[1].matches("/^\\d+$/")) {
                    return VariableType.DOUBLE;
                }
            } else if (t.equals("true") || t.equals("false")) {
                return VariableType.BOOLEAN;
            } else if (t.matches("/^\\d+$/")) {
                return VariableType.INTEGER;
            }

            return VariableType.UNKNOWN;
        }

        private void decodeJSON(JsonObject fromEditor) {
            JsonObject commands = fromEditor.getAsJsonObject("commands");
            JsonArray variables = fromEditor.getAsJsonArray("variables");

            for (int i = 0; i < variables.size(); i++) {
                JsonObject variable = variables.get(i).getAsJsonObject();

                String typeS = variable.get("type").getAsString();
                String rawValue = variable.get("data").getAsString();

                VariableType type;
                Object value;
                String name = variable.get("name").getAsString();

                if (typeS.equalsIgnoreCase("string")) {
                    type = VariableType.STRING;
                    try {
                        value = rawValue.substring(1, rawValue.length() - 1);
                    } catch (Exception e) {
                        throw new RuntimeException("There was an issue compiling a string in the auto code! String name: " + name);
                    }
                } else if (typeS.equalsIgnoreCase("number")) {
                    if (rawValue.contains(".")) {
                        type = VariableType.DOUBLE;
                        value = Double.valueOf(rawValue);
                    } else {
                        type = VariableType.INTEGER;
                        value = Integer.valueOf(rawValue);
                    }
                } else if (typeS.equalsIgnoreCase("boolean")) {
                    type = VariableType.BOOLEAN;
                    value = rawValue.equalsIgnoreCase("true");
                } else {
                    type = VariableType.BOOLEAN;
                    value = false;
                }

                AutoVariable newVariable = new AutoVariable(name, type, value);

                globalVariables.add(newVariable);
            }

            ArrayList<Command> decoded = decodeCommands(commands);

            this.commands.addAll(decoded);
        }

        private ArrayList<Command> decodeCommands(JsonObject obj) {
            ArrayList<Command> commands = new ArrayList<>();

            JsonArray rawSubCommands = obj.get("commands").getAsJsonArray();

            for (int i = 0; i < rawSubCommands.size(); i++) {
                commands.add(decodeCommand(rawSubCommands.get(i).getAsJsonObject()));
            }

            return commands;
        }

        private Command decodeCommand(JsonObject obj) {
            String type = obj.get("type").getAsString();

            Class<? extends Command> typeClass = null;

            if (type.startsWith("\"")) {
                //custom command
                for (Class<? extends Command> instantiableCommand : instantiableCommands) {
                    if (instantiableCommand.getName().equalsIgnoreCase(type)) {
                        typeClass = instantiableCommand;
                    }
                }
            } else if (defaultCommands.containsKey(type.toLowerCase())) {
                typeClass = defaultCommands.get(type.toLowerCase());
            }

            if (typeClass == null) {
                return new InstantCommand(() -> {
                    throw new RuntimeException("There was an issue in compiling! Type '" + type + "' doesn't exist!");
                });
            }

            JsonArray args = obj.get("args").getAsJsonArray();
            ArrayList<AutoVariable> argVariables = new ArrayList<>();
            int lambdaPlaceholderAt = -1;

            for (JsonElement jsonarg : args) {
                String arg = jsonarg.getAsString();

                // first, we'll quickly get the type for the arg
                VariableType vtype = testStringForType(arg);

                // if we can't figure it out, then we'll start looking for variables or other things
                if (vtype == VariableType.UNKNOWN) {
                    // for example, it could be a lambda, so we can make note of that
                    if (arg.startsWith("lambda(")) {
                        argVariables.add(new AutoVariable("lambda_placeholder", VariableType.UNKNOWN, (Object) 0));
                        lambdaPlaceholderAt = argVariables.size() - 1;
                    } else {
                        // but it may be a variable, so we can check.
                        boolean wasVariable = false;
                        for (AutoVariable v : globalVariables) {
                            if (v.getName().equals(arg)) {
                                argVariables.add(v);
                                wasVariable = true;
                                break;
                            }
                        }

                        // finally, if we just can't find anything, we'll throw an error.
                        if (!wasVariable) {
                            throw new RuntimeException("Sorry, I don't know the meaning of '" + arg + "' while compiling.");
                        }
                    }
                }

                //if (vtype)
            }

            //now, finally, we'll decode these variables into an actual object
            Object[] commandArgs = new Object[argVariables.size()];

            for (int i = 0; i < argVariables.size(); i++) {
                if (i == lambdaPlaceholderAt) {
                    //if we come by the lambda placeholder, we need to create our lambda then add it.
                    CustomLambda lambda = decodeLambda(obj.get("lambdaVariables").getAsJsonArray(), obj.get("lambda").getAsJsonArray());

                    //what's cool about a custom lambda is that all of the other lambda types can extend from it (except for those that are typed, such as BooleanLambda, but any LambdaFromX works)
                    //it's a bit... risky doing this but whatever
                    commandArgs[i] = lambda;

                    continue;
                }

                if (argVariables.get(i).getType() == VariableType.STRING) {
                    commandArgs[i] = argVariables.get(i).getAsString();
                } else if (argVariables.get(i).getType() == VariableType.BOOLEAN) {
                    commandArgs[i] = argVariables.get(i).getAsBoolean();
                } else if (argVariables.get(i).getType() == VariableType.DOUBLE) {
                    commandArgs[i] = argVariables.get(i).getAsDouble();
                } else if (argVariables.get(i).getType() == VariableType.INTEGER) {
                    commandArgs[i] = argVariables.get(i).getAsInt();
                } else {
                    throw new RuntimeException("Sorry, unable to decode variable '" + argVariables.get(i).getName() + "' in command argument!");
                }

            }

            return null;
        }


        private CustomLambda decodeLambda(JsonArray args, JsonArray expressions) {
            return (Object ...a) -> {

            };
        }
    }

    private static ArrayList<Class<? extends Command>> instantiableCommands;
    private static HashMap<String, Class<? extends Command>> defaultCommands;

    @SafeVarargs
    public static void registerCustomCommands(Class<? extends Command> ...klasses) {
        if (instantiableCommands == null) {
            instantiableCommands = new ArrayList<>();
            defaultCommands = new HashMap<>();

            //we'll add all of the default commands, but use defaultCommands for the commands
            // with nicknames (so in code there's no quotes), but we'll also just put it in the normal list as well
            defaultCommands.put("instant", InstantCommand.class);
            instantiableCommands.add(InstantCommand.class);
            defaultCommands.put("parallel", ParallelCommand.class);
            instantiableCommands.add(ParallelCommand.class);
            defaultCommands.put("sequential", SequentialCommand.class);
            instantiableCommands.add(SequentialCommand.class);
            defaultCommands.put("stop", StallStop.class);
            instantiableCommands.add(StallStop.class);
            defaultCommands.put("wait", WaitCommand.class);
            instantiableCommands.add(WaitCommand.class);
        }

        //if there's some classes provided, we'll add it to the list.
        if (klasses.length > 0)
            instantiableCommands.addAll(Arrays.asList(klasses));
    }
}
