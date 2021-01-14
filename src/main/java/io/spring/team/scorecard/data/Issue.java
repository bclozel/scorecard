package io.spring.team.scorecard.data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Simon Baslé
 */
public class Issue {

	public final int id;
	public final String author;
	public final LocalDateTime createdAt;
	public final boolean isOpen;
	public final LocalDateTime closedAt;
	public final List<String> labels;
	public final List<String> participants;
	public final String milestone;

	public Issue(int id, String author, LocalDateTime createdAt, boolean isOpen, LocalDateTime closedAt, List<String> labels, List<String> participants, String milestone) {
		this.id = id;
		this.author = author;
		this.createdAt = createdAt;
		this.isOpen = isOpen;
		this.closedAt = closedAt;
		this.labels = labels;
		this.participants = participants;
		this.milestone = milestone;
	}

	@Override
	public String toString() {
		return "Issue{" +
				"id=" + id +
				", author='" + author + '\'' +
				", createdAt=" + createdAt +
				", isOpen=" + isOpen +
				", closedAt=" + closedAt +
				", labels=" + labels +
				", participants=" + participants +
				", milestone='" + milestone + '\'' +
				'}';
	}
}
