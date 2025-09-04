package com.example.farmmitra.Repository;

import com.example.farmmitra.model.Message;
import com.example.farmmitra.model.Inquiry;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {

    /**
     * Finds all messages for a specific inquiry, ordered by timestamp.
     * @param inquiry The inquiry for which to find messages.
     * @return A list of messages for the given inquiry, sorted by time.
     */
    List<Message> findByInquiryOrderByTimestampAsc(Inquiry inquiry);
}
