package com.example.demo;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import java.util.List;

@Tag(name = "Tab", description = "Tab Management APIs")
@RestController
public class TabController {

    static Logger log = LoggerFactory.getLogger(TabController.class);

    @GetMapping("/")
    public String index() {
        return "Welcome to the Who Buys Coffee App!";
    }


    /* TABS CRUD ---------------------------------------------------------------------------------------------------- */

    @Operation(
            summary = "Create a Tab",
            description = "")
    @PostMapping(value = "/tab", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public static TabResponse postTab(@Valid @Parameter(description = "Tab JSON") @RequestBody TabRequest request) {
        try {
            Tab tab = TabService.createTab(request.toTab());
            return TabResponse.fromTab(tab);
        } catch (InstanceAlreadyExistsException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.toString(), e);
        }
    }

    @Operation(
            summary = "Update a Tab",
            description = "")
    @PutMapping(path = "/tab", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public static TabResponse putTab(@Valid @Parameter(description = "Tab JSON") @RequestBody TabRequest tabRequest) {
        try {
            Tab tab = tabRequest.toTab();
            TabService.updateTab(tab);
            return TabResponse.fromTab(tab);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "ID '%s' does not exist.".formatted(tabRequest.getId()), e);
        }
    }

    @Operation(
            summary = "Get a Tab",
            description = "")
    @GetMapping(path = "/tab/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public static TabResponse getTab(@Parameter(description = "Tab ID") @PathVariable(required = true) String id) {
        try {
            Tab tab = TabService.readTab(id);
            return TabResponse.fromTab(tab);
        } catch (InstanceNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "ID '%s' does not exist.".formatted(id), e);
        }
    }

    @Operation(
            summary = "Delete a Tab",
            description = "")
    @DeleteMapping(path = "/tab/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public static TabResponse deleteTab(@Parameter(description = "Tab ID") @PathVariable(required = true) String id) {
        try {
            Tab tab = TabService.deleteTab(id);
            return TabResponse.fromTab(tab);
        } catch (NullPointerException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "ID '%s' does not exist.".formatted(id), e);
        }
    }

    /* BIZ LOGIC ---------------------------------------------------------------------------------------------------- */

    @Operation(
            summary = "Get who pays by day count",
            description = "")
    @GetMapping(value = "/tab/{id}/day/{num}", produces = MediaType.APPLICATION_JSON_VALUE)
    public static TabPayerResponse getWhoPays(
            @Parameter(description = "Tab ID") @PathVariable(required = true) String id,
            @Parameter(description = "Day Number, starting from 1") @PathVariable(required = true) Integer num) {
        try {
            return TabService.getWhoPays(id, num);
        } catch (InstanceNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Operation(
            summary = "Get who pays today by date",
            description = "")
    @GetMapping(value = "/tab/{id}/today/", produces = MediaType.APPLICATION_JSON_VALUE)
    public static TabPayerResponse getWhoPays(@Parameter(description = "Tab ID") @PathVariable(required = true) String id) {
        try {
            return TabService.getWhoPays(id);
        } catch (InstanceNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Operation(
            summary = "Get payer schedule",
            description = "")
    @GetMapping(value = "/tab/{id}/schedule/{num}", produces = MediaType.APPLICATION_JSON_VALUE)
    public static TabScheduleResponse getSchedule(
            @Parameter(description = "Tab ID") @PathVariable(required = true) String id,
            @Parameter(description = "Number of Days on the Schedule") @PathVariable(required = true) Integer num) {
        // TODO: Max number of days on the schedule?
        try {
            return TabService.getSchedule(id, num);
        } catch (InstanceNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

}