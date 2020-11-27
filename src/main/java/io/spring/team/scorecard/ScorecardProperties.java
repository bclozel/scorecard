package io.spring.team.scorecard;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "scorecard")
public class ScorecardProperties {

	private final GitHub github = new GitHub();

	private final Project project = new Project();

	private final IssueLabels labels = new IssueLabels();

	private final Milestones milestones = new Milestones();

	public GitHub getGithub() {
		return this.github;
	}

	public Project getProject() {
		return this.project;
	}

	public Milestones getMilestones() {
		return this.milestones;
	}

	public IssueLabels getLabels() {
		return this.labels;
	}

	public static class GitHub {

		/**
		 * GitHub access token, to be created at https://github.com/settings/tokens
		 */
		private String token;

		public String getToken() {
			return this.token;
		}

		public void setToken(String token) {
			this.token = token;
		}
	}

	public static class Project {

		/**
		 * GitHub organization.
 		 */
		private String org = "spring-projects";

		/**
		 * GitHub project name.
		 */
		private String name;

		/**
		 * List of team member logins.
		 */
		private List<String> members;

		/**
		 * List of bots accounts logins.
		 */
		private List<String> bots;

		public String getOrg() {
			return this.org;
		}

		public void setOrg(String org) {
			this.org = org;
		}

		public String getName() {
			return this.name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getRepository() {
			return this.org + "/" + this.name;
		}

		public List<String> getMembers() {
			return this.members;
		}

		public void setMembers(List<String> members) {
			this.members = members;
		}

		public List<String> getBots() {
			return this.bots;
		}

		public void setBots(List<String> bots) {
			this.bots = bots;
		}
	}

	public static class Milestones {

		/**
		 * List of milestones considered as "backlog", i.e. not associated with a proper release or generation.
		 */
		private List<String> backlog;

		public List<String> getBacklog() {
			return this.backlog;
		}

		public void setBacklog(List<String> backlog) {
			this.backlog = backlog;
		}
	}

	public static class IssueLabels {

		/**
		 * List of labels used for enhancement issues.
		 */
		private List<String> enhancements;

		/**
		 * List of labels used for bugs and regressions.
		 */
		private List<String> bugs;

		/**
		 * List of labels used for tasks and dependency upgrades.
		 */
		private List<String> tasks;

		/**
		 * List of labels used for documentation issues.
		 */
		private List<String> docs;

		/**
		 * List of labels used for duplicate or superseded issues.
		 */
		private List<String> duplicates;

		/**
		 * List of labels used for invalid bugs, declined enhancements or issues for external projects.
		 */
		private List<String> rejected;

		/**
		 * List of labels used for backports and forward-ports.
		 */
		private List<String> ports;

		/**
		 * List of labels used for tagging questions.
		 */
		private List<String> questions;

		/**
		 * List of labels used for blocked or on-hold issues.
		 */
		private List<String> blocked;

		/**
		 * List of labels used for issues pending design work.
		 */
		private List<String> design;

		/**
		 * List of labels used for issues that are waiting for triage.
		 */
		private List<String> triage;

		public List<String> getEnhancements() {
			return this.enhancements;
		}

		public void setEnhancements(List<String> enhancements) {
			this.enhancements = enhancements;
		}

		public List<String> getBugs() {
			return this.bugs;
		}

		public void setBugs(List<String> bugs) {
			this.bugs = bugs;
		}

		public List<String> getTasks() {
			return this.tasks;
		}

		public void setTasks(List<String> tasks) {
			this.tasks = tasks;
		}

		public List<String> getDocs() {
			return this.docs;
		}

		public void setDocs(List<String> docs) {
			this.docs = docs;
		}

		public List<String> getDuplicates() {
			return this.duplicates;
		}

		public void setDuplicates(List<String> duplicates) {
			this.duplicates = duplicates;
		}

		public List<String> getRejected() {
			return this.rejected;
		}

		public void setRejected(List<String> rejected) {
			this.rejected = rejected;
		}

		public List<String> getPorts() {
			return this.ports;
		}

		public void setPorts(List<String> ports) {
			this.ports = ports;
		}

		public List<String> getQuestions() {
			return this.questions;
		}

		public void setQuestions(List<String> questions) {
			this.questions = questions;
		}

		public List<String> getBlocked() {
			return this.blocked;
		}

		public void setBlocked(List<String> blocked) {
			this.blocked = blocked;
		}

		public List<String> getDesign() {
			return this.design;
		}

		public void setDesign(List<String> design) {
			this.design = design;
		}

		public List<String> getTriage() {
			return this.triage;
		}

		public void setTriage(List<String> triage) {
			this.triage = triage;
		}
	}
}
