package ru.practicum.ewm.request.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.ewm.dictionary.RequestStates;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;


@Entity
@Table(name = "requests")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Request {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "created")
    private LocalDateTime created;
    @ManyToOne
    @JoinColumn(name = "event")
    private Event event;
    @ManyToOne
    @JoinColumn(name = "requester")
    private User requester;
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private RequestStates status;
}
