package pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.user.grpc;

import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.common.dtos.user.Role;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.common.dtos.user.State;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.user.UserService;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.user.domain.Member;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.user.domain.User;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.proto.*;


@GrpcService
public class UserServiceImpl extends UserServiceGrpc.UserServiceImplBase {

    private final UserService userService;

    public UserServiceImpl(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void createVolunteer(CreateUserRequest request, StreamObserver<CreateUserResponse> responseObserver) {
        Integer id = userService.createVolunteer(
                request.getName(), request.getUsername(), request.getEmail(), State.valueOf(request.getState())
        );
        CreateUserResponse response = CreateUserResponse.newBuilder().setId(id).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void createMember(CreateUserRequest request, StreamObserver<CreateUserResponse> responseObserver) {
        Integer id = userService.createMember(
                request.getName(), request.getUsername(), request.getEmail(), State.valueOf(request.getState())
        );
        CreateUserResponse response = CreateUserResponse.newBuilder().setId(id).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void createAdmin(CreateUserRequest request, StreamObserver<CreateUserResponse> responseObserver) {
        Integer id = userService.createAdmin(
                request.getName(), request.getUsername(), request.getEmail(), State.valueOf(request.getState())
        );
        CreateUserResponse response = CreateUserResponse.newBuilder().setId(id).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }



    @Override
    public void getUserById(GetUserRequest request, StreamObserver<UserDto> responseObserver) {
        User user = userService.getUserById(request.getId());
        int institutionId = -1;
        boolean institutionActive = false;
        boolean active = user.getState() == State.ACTIVE;
        if (user.getRole() == Role.MEMBER) {
            institutionId = (user instanceof Member) ? ((Member) user).getInstitution().getId() : -1;
            institutionActive = user instanceof Member && ((Member) user).getInstitution().isActive();
        }

        UserDto userDto = UserDto.newBuilder()
                .setId(user.getId())
                .setUsername(user.getUsername())
                .setEmail(user.getEmail())
                .setRole(user.getRole().name())
                .setActive(active)
                .setInstitutionId(institutionId)
                .setInstitutionActive(institutionActive)
                .build();
        responseObserver.onNext(userDto);
        responseObserver.onCompleted();
    }

    @Override
    public void getUserState(GetUserRequest request, StreamObserver<UserStateResponse> responseObserver) {
        String state = userService.getUserState(request.getId()).name();
        responseObserver.onNext(UserStateResponse.newBuilder().setState(state).build());
        responseObserver.onCompleted();
    }

    @Override
    public void changeState(ChangeStateRequest request, StreamObserver<Empty> responseObserver) {
        userService.changeState(request.getId(), State.valueOf(request.getState()));
        responseObserver.onNext(Empty.newBuilder().build());
        responseObserver.onCompleted();
    }
}