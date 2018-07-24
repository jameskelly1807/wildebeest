package co.mv.wb.cli;

import picocli.CommandLine;

@CommandLine.Command(name = "jumpstate",
	description = "jumpstate description",
	subcommands = CommandLine.HelpCommand.class)
public class JumpStateCommand extends SharedCommands
{

	@CommandLine.Option(names = {"-t", "--target-state"}, description = "Target state")
	String targetState;
}
