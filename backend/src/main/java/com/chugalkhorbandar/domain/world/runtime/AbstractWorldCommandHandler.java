package com.chugalkhorbandar.domain.world.runtime;

abstract class AbstractWorldCommandHandler<T extends com.chugalkhorbandar.domain.world.commands.WorldCommand>
        implements WorldCommandHandler<T> {

    private final Class<T> commandType;

    protected AbstractWorldCommandHandler(Class<T> commandType) {
        this.commandType = commandType;
    }

    @Override
    public boolean supports(com.chugalkhorbandar.domain.world.commands.WorldCommand command) {
        return commandType.isInstance(command);
    }
}
