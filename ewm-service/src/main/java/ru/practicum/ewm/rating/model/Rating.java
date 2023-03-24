package ru.practicum.ewm.rating.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.user.model.User;

import javax.persistence.*;


@Entity
@Table(name = "ratings")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Rating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "visitor")
    private User visitor;
    @ManyToOne
    @JoinColumn(name = "event")
    private Event event;
    @Column(name = "liked")
    private Boolean liked;
    @Column(name = "disliked")
    private Boolean disliked;

}
