package main.webapp.pojo;

import java.io.Serializable;

public class ChampionshipRank  implements Serializable {
	
	private Team team;
	private Serie serie;
	
	private Integer position;
	private Integer played;
	private Integer wins;
	private Integer losses;
	private Integer draws;
	private Integer forfeits;
	private Integer goal_for;
	private Integer goal_against;
	private Integer points;
	private Integer wins_home;
	private Integer wins_away;
	private Integer losses_home;
	private Integer losses_away;
	private Integer draws_home;
	private Integer draws_away;
	private Integer forfeits_home;
	private Integer forfeits_away;
	private Integer score_for_home;
	private Integer score_for_away;
	private Integer score_against_home;
	private Integer score_against_away;
	public Team getTeam() {
		return team;
	}
	public void setTeam(Team team) {
		this.team = team;
	}
	public Integer getPlayed() {
		return played;
	}
	public void setPlayed(Integer played) {
		this.played = played;
	}
	public Integer getWins() {
		return wins;
	}
	public void setWins(Integer wins) {
		this.wins = wins;
	}
	public Integer getLosses() {
		return losses;
	}
	public void setLosses(Integer losses) {
		this.losses = losses;
	}
	public Integer getDraws() {
		return draws;
	}
	public void setDraws(Integer draws) {
		this.draws = draws;
	}
	public Integer getForfeits() {
		return forfeits;
	}
	public void setForfeits(Integer forfeits) {
		this.forfeits = forfeits;
	}
	public Integer getGoal_for() {
		return goal_for;
	}
	public void setGoal_for(Integer goal_for) {
		this.goal_for = goal_for;
	}
	public Integer getGoal_against() {
		return goal_against;
	}
	public void setGoal_against(Integer goal_against) {
		this.goal_against = goal_against;
	}
	public Integer getPoints() {
		return points;
	}
	public void setPoints(Integer points) {
		this.points = points;
	}
	public Integer getWins_away() {
		return wins_away;
	}
	public void setWins_away(Integer wins_away) {
		this.wins_away = wins_away;
	}
	public Integer getWins_home() {
		return wins_home;
	}
	public void setWins_home(Integer wins_home) {
		this.wins_home = wins_home;
	}
	public Integer getLosses_home() {
		return losses_home;
	}
	public void setLosses_home(Integer losses_home) {
		this.losses_home = losses_home;
	}
	public Integer getLosses_away() {
		return losses_away;
	}
	public void setLosses_away(Integer losses_away) {
		this.losses_away = losses_away;
	}
	public Integer getDraws_home() {
		return draws_home;
	}
	public void setDraws_home(Integer draws_home) {
		this.draws_home = draws_home;
	}
	public Integer getDraws_away() {
		return draws_away;
	}
	public void setDraws_away(Integer draws_away) {
		this.draws_away = draws_away;
	}
	public Integer getForfeits_home() {
		return forfeits_home;
	}
	public void setForfeits_home(Integer forfeits_home) {
		this.forfeits_home = forfeits_home;
	}
	public Integer getForfeits_away() {
		return forfeits_away;
	}
	public void setForfeits_away(Integer forfeits_away) {
		this.forfeits_away = forfeits_away;
	}
	public Integer getScore_for_home() {
		return score_for_home;
	}
	public void setScore_for_home(Integer score_for_home) {
		this.score_for_home = score_for_home;
	}
	public Integer getScore_against_away() {
		return score_against_away;
	}
	public void setScore_against_away(Integer score_against_away) {
		this.score_against_away = score_against_away;
	}
	public Integer getPosition() {
		return position;
	}
	public void setPosition(Integer position) {
		this.position = position;
	}
	public Integer getScore_for_away() {
		return score_for_away;
	}
	public void setScore_for_away(Integer score_for_away) {
		this.score_for_away = score_for_away;
	}
	public Integer getScore_against_home() {
		return score_against_home;
	}
	public void setScore_against_home(Integer score_against_home) {
		this.score_against_home = score_against_home;
	}
	public Serie getSerie() {
		return serie;
	}
	public void setSerie(Serie serie) {
		this.serie = serie;
	}

}
