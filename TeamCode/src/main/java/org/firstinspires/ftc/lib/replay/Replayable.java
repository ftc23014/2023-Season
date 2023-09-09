package org.firstinspires.ftc.lib.replay;

import org.firstinspires.ftc.lib.replay.log.Log;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

public abstract class Replayable {

    private HashMap<String, String> replayLogs = new HashMap<>();
    private HashMap<String, Method> replaySaveMethods = new HashMap<>();
    private HashMap<String, Field> replayFields = new HashMap<>();

    //The load methods are any linked replay methods
    private HashMap<String, Method> replayLoadMethods = new HashMap<>();

    @Replay(name = "in_replay")
    private boolean m_inReplay = false;

    public Replayable() {
        Method[] methods = this.getClass().getSuperclass().getDeclaredMethods();
        Field[] fields = this.getClass().getSuperclass().getDeclaredFields();

        ArrayList<String> unlinkedWriteMethods = new ArrayList<>();
        ArrayList<String> waitingFor = new ArrayList<>();

        ArrayList<String> names = new ArrayList<>();

        boolean movedToMethods = false;

        //Loop through all methods
        for (int i = 0; i < methods.length + fields.length; i++) {
            Method method = null;
            Field field = null;
            Log logAnnotation;

            boolean objectWasAccessible = false;

            if (i < fields.length) {
                field = fields[i];

                logAnnotation = field.getAnnotation(Log.class);

                objectWasAccessible = field.isAccessible();

                field.setAccessible(true);
            } else {
                if (!movedToMethods) {
                    movedToMethods = true;
                }

                method = methods[i - fields.length];

                logAnnotation = method.getAnnotation(Log.class);

                objectWasAccessible = method.isAccessible();

                method.setAccessible(true);
            }

            //If the method has the AutoLog annotation, it's a write to the replay file method (save method)
            if (logAnnotation != null) {
                String link = logAnnotation.link();

                //If is a method, add it to the list of unlinked write methods
                //Methods need a write method to be linked to
                if (movedToMethods) {
                    String methodSaveName = logAnnotation.name().isEmpty() ? method.getName() : logAnnotation.name();
                    names.add(methodSaveName);

                    if (link.isEmpty()) continue;

                    replayLogs.put(methodSaveName, link);
                    replaySaveMethods.put(methodSaveName, method);

                    if (unlinkedWriteMethods.contains(link)) {
                        unlinkedWriteMethods.remove(link);
                    } else {
                        waitingFor.add(link);
                    }
                } else {
                    //Fields don't need a write method to be linked to
                    //Since they'll be written to themselves
                    //Unless, they have a replaylink annotation

                    String fieldSaveName = (logAnnotation.name().isEmpty() ? field.getName() : logAnnotation.name());

                    names.add(fieldSaveName);

                    if (link.isEmpty()) {
                        replayLogs.put(fieldSaveName, fieldSaveName);
                        replayFields.put(fieldSaveName, field);
                    } else {
                        replayLogs.put(fieldSaveName, link);
                        replayFields.put(fieldSaveName, field);

                        if (unlinkedWriteMethods.contains(link)) {
                            unlinkedWriteMethods.remove(link);
                        } else {
                            waitingFor.add(link);
                        }
                    }
                }
            }

            //Get the IOMethod annotation of the method (fields shouldn't have IOMethod annotations)
            Replay replayMethod = movedToMethods ? method.getAnnotation(Replay.class) : null;

            if (replayMethod != null) {
                names.add(replayMethod.name());

                //If the method is not linked to a write to file method yet, add it to the list of unlinked write methods
                if (!replayLogs.containsValue(replayMethod.name())) {
                    unlinkedWriteMethods.add(replayMethod.name());
                }

                waitingFor.remove(replayMethod.name());

                replayLoadMethods.put(replayMethod.name(), method);
            }

            if (field != null) {
                field.setAccessible(objectWasAccessible);
            } else {
                method.setAccessible(objectWasAccessible);
            }
        }

        //Check if there is any methods referenced in links, but are not found in the class.
        if (!waitingFor.isEmpty()) {
            String waitingForString = "";
            for (String string : waitingFor) {
                waitingForString += string + ", ";
            }

            throw new RuntimeException("Replayable methods " + waitingForString + " referenced " + (waitingFor.size() > 1 ? "links that do" : "a link that does") + " not exist! Please make sure you add in the correct annotations.");
        }

        //Check if names are unique
        if (names.size() != names.stream().distinct().count()) {
            throw new RuntimeException("All methods in a replayable class must have unique names.");
        }

        ReplayManager.register(this);
    }

    protected void updateReplayState(boolean inReplay) {
        m_inReplay = inReplay;
    }

    public boolean replaying() {
        return m_inReplay;
    }

    protected void feedThread(LogDataThread thread, int cycle) {
        String spef_name = thread.getSpecificName();

        boolean movedToMethods = false;

        Method[] methods = this.getClass().getDeclaredMethods();
        Field[] fields = this.getClass().getDeclaredFields();

        String linkName = "";

        for (int i = 0; i < methods.length + fields.length; i++) {
            Method method = null;
            Field field = null;

            if (i >= fields.length) {
                if (!movedToMethods) {
                    movedToMethods = true;
                }
            }

            if (movedToMethods) {
                method = methods[i - fields.length];
            } else {
                field = fields[i];
            }

            Log logAnnotation = movedToMethods ? method.getAnnotation(Log.class) : field.getAnnotation(Log.class);
            Replay replayAnnotation = movedToMethods ? method.getAnnotation(Replay.class) : null;

            if (logAnnotation == null) {
                if (replayAnnotation == null) {
                    continue;
                }

                String name = replayAnnotation.name().isEmpty() ? method.getName() : replayAnnotation.name();

                if (!linkName.isEmpty() && name.equals(linkName)) {
                    thread.setMethod(method, cycle, this);
                }

                continue;
            }

            String name = (logAnnotation.name().isEmpty()) ? (movedToMethods ? method.getName() : field.getName()) : logAnnotation.name();

            if (name.equals(spef_name) && linkName.isEmpty()) {
                linkName = logAnnotation.link().isEmpty() ? name : logAnnotation.link();

                //restart the loop
                i = -1;
                movedToMethods = false;
                continue;
            }

            boolean fieldLinkFound = linkName.equals(name) && !movedToMethods;

            if ((name.equals(linkName) && !linkName.isEmpty()) || fieldLinkFound) {
                if (movedToMethods) {
                    thread.setMethod(method, cycle, this);
                } else {
                    thread.setField(field, cycle, this);
                }
            }
        }

        if (linkName.isEmpty()) System.out.println("did not find link name! " + spef_name);
    }

    //Overridable methods
    public abstract String getBaseName();

    public abstract void replayInit();

    public abstract void exitReplay();
}
