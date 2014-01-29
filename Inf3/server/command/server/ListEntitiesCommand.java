package command.server;

import java.util.Collection;

import output.Logger.MessageType;
import server.Server;
import util.ServerConst;

import command.ServerCommand;

import environment.entity.Entity;

public class ListEntitiesCommand extends ServerCommand {

	public ListEntitiesCommand() {
		super(ServerConst.SC_LIST_ENTITIES);
	}

	@Override
	protected int routine(Server _src, String _cmd, StringBuilder _mes) {
		String list = "";
		Collection<Entity> entities = Entity.getEntities();
		for(Entity ent : entities) {
			list += String.format("%d: %s[%d|%d] (%s)\r\n",ent.getId(),ent.getDescription(),ent.getPosition().x,ent.getPosition().y,ent.getClass().getSimpleName());
		}
		_src.getLogger().print(list, MessageType.INFO);
		_mes.append(String.format("Succesfully listed %d entities", entities.size()));
		return 1;
	}
}
