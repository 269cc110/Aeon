package net.cc110.aeon;

public class Permissions
{
	// currently unused
	public static final int UNDELETE		= (1 << 0),
							ADMIN			= (1 << 1),
							MOD				= (1 << 2),
							CLEAR			= (1 << 3),
							SHUTDOWN		= (1 << 4),
							CLEAR_IMMUNITY	= (1 << 5),
							FLOOD_IMMUNITY	= (1 << 6),
							COOLDOWN_BYPASS	= (1 << 7),
							BOT_OP			= (1 << 8);
}
