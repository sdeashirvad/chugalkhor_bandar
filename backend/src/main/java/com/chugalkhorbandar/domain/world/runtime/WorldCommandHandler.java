package com.chugalkhorbandar.domain.world.runtime;

import com.chugalkhorbandar.domain.world.commands.WorldCommand;

public interface WorldCommandHandler<T extends WorldCommand> {

    boolean supports(WorldCommand command);

    WorldState handle(WorldState current, T command);
}
