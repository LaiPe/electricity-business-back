package com.laipe.electricitybusiness.controller;

import com.laipe.electricitybusiness.controller.generic.GenericCreateController;
import com.laipe.electricitybusiness.controller.generic.GenericDeleteController;
import com.laipe.electricitybusiness.controller.generic.GenericReadController;
import com.laipe.electricitybusiness.controller.generic.GenericUpdateController;
import com.laipe.electricitybusiness.controller.handler.ResourceNotFoundException;
import com.laipe.electricitybusiness.dto.user.GetUserDTO;
import com.laipe.electricitybusiness.dto.user.GetUserMapper;
import com.laipe.electricitybusiness.dto.user.PostUserDTO;
import com.laipe.electricitybusiness.dto.user.PostUserMapper;
import com.laipe.electricitybusiness.model.User;
import com.laipe.electricitybusiness.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController()
@RequestMapping("/users")
public class UserController {

    private final GenericCreateController<User,GetUserDTO,PostUserDTO,Long> createController;
    private final GenericReadController<User,GetUserDTO,Long> readController;
    private final GenericUpdateController<User,GetUserDTO,PostUserDTO,Long> updateController;
    private final GenericDeleteController<User,GetUserDTO,Long> deleteController;

    public UserController(
            UserService service,
            GetUserMapper getUserMapper,
            PostUserMapper postUserMapper
    ) {
        this.createController = new GenericCreateController<>(
                service,
                getUserMapper,
                postUserMapper
        ){};

        this.readController = new GenericReadController<>(
                service,
                getUserMapper,
                User.class
        ){};

        this.updateController = new GenericUpdateController<>(
                service,
                getUserMapper,
                postUserMapper,
                User.class
        ){};

        this.deleteController = new GenericDeleteController<>(
                service,
                getUserMapper,
                User.class
        ){};
    }

    @PostMapping
    public ResponseEntity<GetUserDTO> create(@RequestBody @Valid PostUserDTO postUserDTO) {
        return createController.create(postUserDTO);
    }

    @GetMapping
    public ResponseEntity<List<GetUserDTO>> getAll() {
        return readController.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<GetUserDTO> getById(@PathVariable Long id) throws ResourceNotFoundException {
        return readController.getById(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<GetUserDTO> updateById(@PathVariable Long id, @RequestBody @Valid PostUserDTO postDTO) {
        return updateController.updateById(id, postDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<GetUserDTO> deleteById(@PathVariable Long id) {
        return deleteController.deleteById(id);
    }
}
