package ru.practicum.ewm.request.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.jackson.Jacksonized;
import ru.practicum.ewm.dictionary.RequestStates;

import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@Builder
@Jacksonized
public class ParticipationRequestDto {
    private String created;
    private Long event;
    private Long id;
    private Long requester;
    private RequestStates status;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ParticipationRequestDto that = (ParticipationRequestDto) o;
        return Objects.equals(created, that.created) && Objects.equals(event, that.event) && Objects.equals(id, that.id) && Objects.equals(requester, that.requester) && Objects.equals(status, that.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(created, event, id, requester, status);
    }
}
