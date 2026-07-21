package com.dducwsjvbe.borrowing_service.command.data;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "borrowing")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Borrowing {
    @Id
    private String id;

    @Column(name = "book_id")
    private String bookId;

    @Column(name = "employee_id")
    private String employeeId;

    @Column(name = "borrow_date")
    private Date borrowingDate;

    @Column(name = "return_date")
    private Date returnDate;
}
