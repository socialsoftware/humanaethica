package pt.ulisboa.tecnico.socialsoftware.humanaethica.common.events.user;

import pt.ulisboa.tecnico.socialsoftware.humanaethica.common.dtos.auth.Type;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.common.dtos.user.RegisterUserDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.common.dtos.user.Role;

public class UserRegisteredEvent {

    private final RegisterUserDto registerUserDto;
    private final Integer userId;
    private final Type type;
    private final Role role;
    private final String name;

    public UserRegisteredEvent(RegisterUserDto dto, Integer userId, Type type, Role role, String name) {
        this.registerUserDto = dto;
        this.userId = userId;
        this.type = type;
        this.role = role;
        this.name = name;
    }

    public RegisterUserDto getRegisterUserDto() {
        return registerUserDto;
    }

    public Integer getUserId() {
        return userId;
    }

    public Type getType() {
        return type;
    }

    public Role getRole() {
        return role;
    }

    public String getName() {
        return name;
    }
}
