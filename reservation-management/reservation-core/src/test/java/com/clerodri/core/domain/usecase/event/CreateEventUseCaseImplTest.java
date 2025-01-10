package com.clerodri.core.domain.usecase.event;

import com.clerodri.core.domain.model.Event;
import com.clerodri.core.domain.repository.EventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CreateEventUseCaseImplTest {

    @Mock
    EventRepository eventRepository;


    @InjectMocks
    CreateEventUseCaseImpl createEventUseCase;

    private Event event;

    @BeforeEach
    public void init (){
        event = new Event();

    }

    @Test
    public void createShouldSaveEventSuccessfully(){

        when(eventRepository.save(event)).thenReturn(event);

        Event eventSaved = createEventUseCase.create(event);

        verify(eventRepository,times(1)).save(any(Event.class));
        assertNotNull(eventSaved);
    }
}