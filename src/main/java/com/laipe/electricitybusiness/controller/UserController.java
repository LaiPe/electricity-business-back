package com.laipe.electricitybusiness.controller;

import com.laipe.electricitybusiness.controller.generic.*;
import com.laipe.electricitybusiness.controller.handler.ResourceNotFoundException;
import com.laipe.electricitybusiness.dto.user.*;
import com.laipe.electricitybusiness.model.User;
import com.laipe.electricitybusiness.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController()
@RequestMapping("/users")
public class UserController
        implements GenericController<Long, GetStrictUserDTO, PostUserDTO, GetStrictUserDTO, GetFullUserDTO, GetFullUserDTO, PostUserDTO, GetStrictUserDTO> {

    private final GenericCreateController<User, GetStrictUserDTO,PostUserDTO,Long> createController;
    private final GenericReadController<User, GetStrictUserDTO, GetFullUserDTO,Long> readController;
    private final GenericUpdateController<User, GetFullUserDTO,PostUserDTO,Long> updateController;
    private final GenericDeleteController<User, GetStrictUserDTO,Long> deleteController;

    public UserController(
            UserService service,
            GetFullUserMapper getFullUserMapper,
            GetStrictUserMapper getStrictUserMapper,
            PostUserMapper postUserMapper
    ) {
        this.createController = new GenericCreateController<>(
                service,
                getStrictUserMapper,
                postUserMapper
        ){};

        this.readController = new GenericReadController<>(
                service,
                getStrictUserMapper,
                getFullUserMapper,
                User.class
        ){};

        this.updateController = new GenericUpdateController<>(
                service,
                getFullUserMapper,
                postUserMapper,
                User.class
        ){};

        this.deleteController = new GenericDeleteController<>(
                service,
                getStrictUserMapper,
                User.class
        ){};
    }

    @PostMapping
    public ResponseEntity<GetStrictUserDTO> create(@RequestBody @Valid PostUserDTO postUserDTO) {
        return createController.create(postUserDTO);
    }

    @GetMapping
    public ResponseEntity<List<GetStrictUserDTO>> getAll() {
        return readController.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<GetFullUserDTO> getById(@PathVariable Long id) throws ResourceNotFoundException {
        return readController.getById(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<GetFullUserDTO> updateById(@PathVariable Long id, @RequestBody @Valid PostUserDTO postDTO) {
        return updateController.updateById(id, postDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<GetStrictUserDTO> deleteById(@PathVariable Long id) {
        return deleteController.deleteById(id);
    }
}
