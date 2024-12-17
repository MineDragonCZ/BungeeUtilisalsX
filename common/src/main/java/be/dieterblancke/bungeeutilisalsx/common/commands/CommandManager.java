package be.dieterblancke.bungeeutilisalsx.common.commands;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.command.Command;
import be.dieterblancke.bungeeutilisalsx.common.api.command.CommandBuilder;
import be.dieterblancke.bungeeutilisalsx.common.api.command.CommandCall;
import be.dieterblancke.bungeeutilisalsx.common.api.server.IProxyServer;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.ConfigFiles;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.server.ServerGroup;
import be.dieterblancke.bungeeutilisalsx.common.commands.domains.DomainsCommandCall;
import be.dieterblancke.bungeeutilisalsx.common.commands.general.*;
import be.dieterblancke.bungeeutilisalsx.common.commands.general.message.*;
import be.dieterblancke.bungeeutilisalsx.common.commands.general.spy.CommandSpyCommandCall;
import be.dieterblancke.bungeeutilisalsx.common.commands.general.spy.SocialSpyCommandCall;
import be.dieterblancke.bungeeutilisalsx.common.commands.plugin.PluginCommandCall;
import be.dieterblancke.configuration.api.IConfiguration;
import be.dieterblancke.configuration.api.ISection;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class CommandManager {

    protected final List<Command> commands = Lists.newArrayList();

    public void load() {
        if (!commands.isEmpty()) {
            this.unregisterAll();
        }

        this.registerGeneralCommands();
        this.registerCustomCommands();
    }

    protected void registerGeneralCommands() {
        //registerGeneralCommand("bungeeutilisals", new PluginCommandCall());
        registerGeneralCommand("server", new ServerCommandCall());
        registerGeneralCommand("find", new FindCommandCall());
        registerGeneralCommand("glag", new GLagCommandCall());
        registerGeneralCommand("clearchat", new ClearChatCommandCall());
        registerGeneralCommand("ping", new PingCommandCall());
        registerGeneralCommand("glist", new GListCommandCall());
        registerGeneralCommand("socialspy", new SocialSpyCommandCall());
        registerGeneralCommand("commandspy", new CommandSpyCommandCall());
        registerGeneralCommand("msg", new MsgCommandCall());
        registerGeneralCommand("reply", new ReplyCommandCall());
        registerGeneralCommand("ignore", new IgnoreCommandCall());
        registerGeneralCommand("msgtoggle", new MsgToggleCommandCall());
        registerGeneralCommand("domains", new DomainsCommandCall());
        registerGeneralCommand("offlinemessage", new OfflineMessageCommandCall());

        if (ConfigFiles.GENERALCOMMANDS.getConfig().getBoolean("server.slash-server.enabled")) {
            registerSlashServerCommands();
        }
    }

    protected void registerSlashServerCommands() {
        final String permission = ConfigFiles.GENERALCOMMANDS.getConfig().getString("server.slash-server.permission");

        for (IProxyServer proxyServer : BuX.getInstance().serverOperations().getServers()) {
            final String name = proxyServer.getName().toLowerCase();
            final CommandBuilder builder = CommandBuilder.builder()
                    .enabled(true)
                    .name(name)
                    .permission(permission.replace("{server}", name))
                    .executable(new SlashServerCommandCall(name));

            buildCommand(name, builder);
        }
    }

    protected void registerCustomCommands() {
        final IConfiguration config = ConfigFiles.CUSTOMCOMMANDS.getConfig();

        for (ISection section : config.getSectionList("commands")) {
            final String name = section.getString("name");
            final List<String> aliases = section.exists("aliases") ? section.getStringList("aliases") : Lists.newArrayList();
            final String permission = section.exists("permission") ? section.getString("permission") : null;
            final List<String> commands = section.exists("execute") ? section.getStringList("execute") : Lists.newArrayList();
            final String server = section.exists("server") ? section.getString("server") : "ALL";
            final List<ServerGroup> disabledServers = section.exists("disabled-servers") ? section.getStringList("disabled-servers")
                    .stream()
                    .filter(s -> ConfigFiles.SERVERGROUPS.getServers().containsKey(s))
                    .map(s -> ConfigFiles.SERVERGROUPS.getServer(s))
                    .flatMap(Optional::stream)
                    .collect(Collectors.toList()) : new ArrayList<>();
            final boolean listenerBased = section.exists("listener-based") && section.getBoolean("listener-based");

            final CommandBuilder commandBuilder = CommandBuilder.builder()
                    .enabled(true)
                    .name(name)
                    .aliases(aliases)
                    .permission(permission)
                    .disabledServers(disabledServers)
                    .listenerBased(listenerBased)
                    .executable(new CustomCommandCall(section, server, commands));

            buildCommand(name, commandBuilder);
        }
    }

    public void registerGeneralCommand(final String section, final CommandCall call) {
        this.registerGeneralCommand(section, call, new ArrayList<>());
    }

    public void registerGeneralCommand(final String section, final CommandCall call, final List<String> parameters) {
        final CommandBuilder commandBuilder = CommandBuilder.builder()
                .name(section)
                .fromSection(ConfigFiles.GENERALCOMMANDS.getConfig().getSection(section))
                .parameters(parameters)
                .executable(call);

        buildCommand(section, commandBuilder);
    }

    protected void buildCommand(final String name, final CommandBuilder builder) {
        final Command command = builder.build();

        if (command != null) {
            command.register();

            commands.add(command);
            BuX.debug("Registered a command named " + command.getName() + ".");
        } else {
            BuX.debug("Skipping registration of a command named " + name + ".");
        }
    }

    public void unregisterAll() {
        for (Command command : commands) {
            command.unload();
        }
        commands.clear();
    }

    public Optional<Command> findCommandByName(final String name) {
        return this.commands.stream()
                .filter(command ->
                        command.getName().equalsIgnoreCase(name)
                                || Arrays.stream(command.getAliases()).anyMatch(alias -> alias.equalsIgnoreCase(name)))
                .findFirst();
    }

    private List<String> getParameterList(final ISection parameterSection) {
        final List<String> parameters = Lists.newArrayList();

        for (String key : parameterSection.getKeys()) {
            if (parameterSection.getBoolean(key)) {
                parameters.add("-" + key);
            }
        }

        return parameters;
    }
}
