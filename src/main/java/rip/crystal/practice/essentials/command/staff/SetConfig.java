package rip.crystal.practice.essentials.command.staff;

import org.bukkit.entity.Player;
import rip.crystal.practice.api.command.BaseCommand;
import rip.crystal.practice.api.command.Command;
import rip.crystal.practice.api.command.CommandArgs;
import rip.crystal.practice.utilities.chat.CC;
import rip.crystal.practice.utilities.file.type.BasicConfigurationFile;
import rip.crystal.practice.cPractice;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class SetConfig extends BaseCommand {

    @Command(name = "setconfig", permission = "cpractice.command.setconfig")
    @Override
    public void onCommand(CommandArgs commandArgs) {
        Player player = commandArgs.getPlayer();
        String[] args = commandArgs.getArgs();
        if (args.length != 3) return;

        BasicConfigurationFile config = getConfigByName(args[0]);
        if (config == null) {
            player.sendMessage("Couldn't find a getter for a config named: '" + args[0] + "'");
            return;
        }

        String path = args[1];
        String value = args[2];
        Class<?> clazz = config.get(path).getClass();

        try {
            config.getConfiguration().set(path, convert(clazz, value));
        } catch (ClassCastException e) {
            player.sendMessage(e.getMessage());
            return;
        }

        config.save();
        player.sendMessage("Successfully updated '" + path + "' to " + value + " of type " + clazz.getName() + " in config '" + config.getName() + "'");
    }

    private BasicConfigurationFile getConfigByName(String configName) {
        try {
            configName = configName.substring(0, 1).toUpperCase() + configName.substring(1);
            Method method = cPractice.class.getMethod("get" + configName); // Method returning BaseConfigurationFile
            return (BasicConfigurationFile) method.invoke(cPractice.get());
        } catch (Exception e) {
            return null;
        }
    }

    public static <T> T convert(Class<T> type, Object value) {
        if (value == null) {
            return null;
        }

        if (type.isInstance(value)) {
            return type.cast(value);
        }

        try {
            if (type == String.class) {
                return type.cast(value.toString());
            } else if (type == Integer.class || type == int.class) {
                return type.cast(Integer.valueOf(value.toString()));
            } else if (type == Double.class || type == double.class) {
                return type.cast(Double.valueOf(value.toString()));
            } else if (type == Boolean.class || type == boolean.class) {
                return type.cast(Boolean.valueOf(value.toString()));
            } else if (type == Long.class || type == long.class) {
                return type.cast(Long.valueOf(value.toString()));
            } else if (type == Float.class || type == float.class) {
                return type.cast(Float.valueOf(value.toString()));
            } else if (type == Short.class || type == short.class) {
                return type.cast(Short.valueOf(value.toString()));
            } else if (type == Byte.class || type == byte.class) {
                return type.cast(Byte.valueOf(value.toString()));
            } else {
                throw new ClassCastException("Cannot convert " + value.getClass().getName() + " to " + type.getName());
            }
        } catch (NumberFormatException e) {
            throw new ClassCastException("Cannot convert " + value.getClass().getName() + " to " + type.getName());
        }
    }
}
