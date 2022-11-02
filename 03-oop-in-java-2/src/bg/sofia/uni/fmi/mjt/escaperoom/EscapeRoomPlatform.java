package bg.sofia.uni.fmi.mjt.escaperoom;

import bg.sofia.uni.fmi.mjt.escaperoom.exception.PlatformCapacityExceededException;
import bg.sofia.uni.fmi.mjt.escaperoom.exception.RoomAlreadyExistsException;
import bg.sofia.uni.fmi.mjt.escaperoom.exception.RoomNotFoundException;
import bg.sofia.uni.fmi.mjt.escaperoom.exception.TeamNotFoundException;
import bg.sofia.uni.fmi.mjt.escaperoom.room.EscapeRoom;
import bg.sofia.uni.fmi.mjt.escaperoom.room.Review;
import bg.sofia.uni.fmi.mjt.escaperoom.team.Team;

import java.util.Objects;

public class EscapeRoomPlatform implements EscapeRoomAdminAPI, EscapeRoomPortalAPI {

    private int roomsIndex = 0;
    private final EscapeRoom[] rooms;
    private final Team[] teams;
    private final int maxCapacity;

    public EscapeRoomPlatform(Team[] teams, int maxCapacity) {
        this.teams = teams;
        this.maxCapacity = maxCapacity;
        rooms = new EscapeRoom[maxCapacity];
    }

    @Override
    public void addEscapeRoom(EscapeRoom room) throws RoomAlreadyExistsException {
        if (room == null) {
            throw new IllegalArgumentException("Invalid room.");
        }

        if (roomsIndex == maxCapacity) {
            throw new PlatformCapacityExceededException("Cannot add more rooms.");
        }

        for (int i = 0; i < roomsIndex; i++) {
            if (Objects.equals(rooms[i].getName(), room.getName())) {
                throw new RoomAlreadyExistsException("Room with the same name already added.");
            }
        }

        rooms[roomsIndex++] = room;
    }

    @Override
    public void removeEscapeRoom(String roomName) throws RoomNotFoundException {

        if (roomName == null || roomName.isBlank()) {
            throw new IllegalArgumentException("");
        }

        boolean isFound = false;

        int i;
        for (i = 0; i < roomsIndex; i++) {
            if (Objects.equals(rooms[i].getName(), roomName)) {
                isFound = true;
                break;
            }
        }

        if (!isFound) {
            throw new RoomNotFoundException("");
        }

        for (; i < roomsIndex - 1; ++i) {
            rooms[i] = rooms[i + 1];
        }

        rooms[i + 1] = null;
        --roomsIndex;
    }

    @Override
    public EscapeRoom[] getAllEscapeRooms() {
        EscapeRoom[] result = new EscapeRoom[roomsIndex];
        System.arraycopy(rooms, 0, result, 0, roomsIndex);
        return result;
    }

    @Override
    public void registerAchievement(String roomName, String teamName, int escapeTime)
            throws RoomNotFoundException, TeamNotFoundException {

        Team team = null;
        EscapeRoom room = getEscapeRoomByName(roomName);

        if (escapeTime <= 0 || escapeTime > room.getMaxTimeToEscape()) {
            throw new IllegalArgumentException();
        }

        if (teamName == null || teamName.isBlank()) {
            throw new IllegalArgumentException();
        }

        for (Team value : teams) {
            if (Objects.equals(value.getName(), teamName)) {
                team = value;
                break;
            }
        }

        if (team == null) {
            throw new TeamNotFoundException("");
        }

        team.updateRating(room.getDifficulty().getRank());

        if(escapeTime<=room.getMaxTimeToEscape()/2){
            team.updateRating(2);
        }
        else if(escapeTime<=room.getMaxTimeToEscape()*3/4){
            team.updateRating(1);
        }
    }

    @Override
    public EscapeRoom getEscapeRoomByName(String roomName) throws RoomNotFoundException {
        if (roomName == null || roomName.isBlank()) {
            throw new IllegalArgumentException("Invalid room name.");
        }

        for (int i = 0; i < roomsIndex; i++) {
            if (rooms[i].getName().equals(roomName)) {
                return rooms[i];
            }
        }

        throw new RoomNotFoundException("No room with name " + roomName + " found.");
    }

    @Override
    public void reviewEscapeRoom(String roomName, Review review) throws RoomNotFoundException {
        getEscapeRoomByName(roomName).addReview(review);
    }

    @Override
    public Review[] getReviews(String roomName) throws RoomNotFoundException {
        return getEscapeRoomByName(roomName).getReviews();
    }

    @Override
    public Team getTopTeamByRating() {
        if(teams.length==0){
            return null;
        }

        Team top = teams[0];

        for (int i = 1; i < teams.length; i++) {
            if (teams[i].getRating() > top.getRating()) {
                top = teams[i];
            }
        }

        return top;
    }
}
