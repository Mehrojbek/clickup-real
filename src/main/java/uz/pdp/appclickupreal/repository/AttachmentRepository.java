package uz.pdp.appclickupreal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.pdp.appclickupreal.entity.Attachment;

import java.util.UUID;

public interface AttachmentRepository  extends JpaRepository<Attachment, UUID> {

}
