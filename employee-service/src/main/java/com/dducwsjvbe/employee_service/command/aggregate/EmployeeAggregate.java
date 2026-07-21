package com.dducwsjvbe.employee_service.command.aggregate;

import com.dducwsjvbe.employee_service.command.command.CreateEmployeeCommand;
import com.dducwsjvbe.employee_service.command.command.DeleteEmployeeCommand;
import com.dducwsjvbe.employee_service.command.command.UpdateEmployeeCommand;
import com.dducwsjvbe.employee_service.command.event.EmployeeCreatedEvent;
import com.dducwsjvbe.employee_service.command.event.EmployeeDeleteEvent;
import com.dducwsjvbe.employee_service.command.event.EmployeeUpdateEvent;
import lombok.NoArgsConstructor;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import org.springframework.beans.BeanUtils;
import org.springframework.util.StringUtils;

@Aggregate
@NoArgsConstructor
public class EmployeeAggregate {
    @AggregateIdentifier
    private String id;
    private String firstName;
    private String lastName;
    private String kin;
    private Boolean isDisciplined;

    @CommandHandler
    public EmployeeAggregate(CreateEmployeeCommand command) {
        EmployeeCreatedEvent employeeCreatedEvent = new EmployeeCreatedEvent();
        BeanUtils.copyProperties(command, employeeCreatedEvent);
        AggregateLifecycle.apply(employeeCreatedEvent);
    }

    @EventSourcingHandler
    public void on(EmployeeCreatedEvent event) {
        this.id = event.getId();
        this.firstName = event.getFirstName();
        this.lastName = event.getLastName();
        this.kin = event.getKin();
        this.isDisciplined = event.getIsDisciplined();
    }

    @CommandHandler
    public void handle(UpdateEmployeeCommand command) {
        boolean hasAnyField = StringUtils.hasText(command.getFirstName())
                || StringUtils.hasText(command.getLastName())
                || StringUtils.hasText(command.getKin());

        // Không có gì thay đổi → không apply event, trả về luôn
        if (!hasAnyField) {
            return;
        }
        // So sánh với state hiện tại của Aggregate
        boolean isChanged =
                (StringUtils.hasText(command.getFirstName()) && !command.getFirstName().equals(this.firstName))
                        || (StringUtils.hasText(command.getLastName()) && !command.getLastName().equals(this.lastName))
                        || (StringUtils.hasText(command.getKin()) && !command.getKin().equals(this.kin));

        // Không có gì thay đổi thật sự → không apply event
        if (!isChanged) {
            return;
        }
        EmployeeCreatedEvent employeeCreatedEvent = new EmployeeCreatedEvent(
                command.getId(),
                StringUtils.hasText(command.getFirstName()) ? command.getFirstName() : this.firstName,
                StringUtils.hasText(command.getLastName()) ? command.getLastName() : this.lastName,
                StringUtils.hasText(command.getKin()) ? command.getKin() : this.kin,
                Boolean.FALSE
        );
        AggregateLifecycle.apply(employeeCreatedEvent);
    }

    @EventSourcingHandler
    public void on(EmployeeUpdateEvent event) {
        this.id = event.getId();
        this.firstName = event.getFirstName();
        this.lastName = event.getLastName();
        this.kin = event.getKin();
        this.isDisciplined = event.getIsDisciplined();
    }

    @CommandHandler
    public void handle(DeleteEmployeeCommand command) {
        EmployeeDeleteEvent employeeDeleteEvent = new EmployeeDeleteEvent();
        BeanUtils.copyProperties(command, employeeDeleteEvent);
        AggregateLifecycle.apply(employeeDeleteEvent);
    }

    @EventSourcingHandler
    public void on(EmployeeDeleteEvent event) {
        AggregateLifecycle.markDeleted(); //save event để lần sau nếu tìm id đã xóa or notFound thì bắn ra lỗi sync
    }
}
