/**
 * 
 */
package net.milkbowl.administrate;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * @author sleaker
 *
 */
public class PlayerData {
    private String world;
    private int[] xyz;
    private boolean adminMode;
    private boolean god;
    private boolean invisible;
    private boolean noPickup;
    private boolean stealthed;

    private Properties props = null;

    PlayerData() {
        adminMode = false;
        god = false;
        invisible = false;
        noPickup = false;
        stealthed = false;
        props = new Properties();
    }

    /**
     * @return the world
     */
    public String getWorld() {
        return world;
    }
    /**
     * @param world the world to set
     */
    public void setWorld(String world) {
        this.world = world;
    }
    /**
     * @return the xyz
     */
    public int[] getXyz() {
        return xyz;
    }

    /**
     * @param xyz the xyz to set
     */
    public void setXyz(int[] xyz) {
        this.xyz = xyz;
    }

    /**
     * @return the AdminMode
     */
    public boolean isAdminMode() {
        return adminMode;
    }

    /**
     * @param enabled the enabled to set
     */
    public void setAdminMode(boolean val) {
        this.adminMode = val;
        this.god = val;
        this.invisible = val;
        this.noPickup = val;
        this.stealthed = val;
    }

    /**
     * @return the isGod
     */
    public boolean isGod() {
        return god;
    }

    /**
     * @param isGod the isGod to set
     */
    public void setGod(boolean isGod) {
        this.god = isGod;
    }

    /**
     * @return the invisible
     */
    public boolean isInvisible() {
        return invisible;
    }

    /**
     * @param invisible the invisible to set
     */
    public void setInvisible(boolean invisible) {
        this.invisible = invisible;
    }

    /**
     * @return the noPickup
     */
    public boolean isNoPickup() {
        return noPickup;
    }

    /**
     * @param noPickup the noPickup to set
     */
    public void setNoPickup(boolean noPickup) {
        this.noPickup = noPickup;
    }

    /**
     * @return the isStealthed
     */
    public boolean isStealthed() {
        return stealthed;
    }

    /**
     * @param isStealthed the isStealthed to set
     */
    public void setStealthed(boolean isStealthed) {
        this.stealthed = isStealthed;
    }

    /**
     * Returns a string representation of the location
     * 
     * @return String
     */
    public String locationString() {
        String split = ",";
        if (world != null || xyz != null)
            return world + split + " x: " + xyz[0] + split + " y: " + xyz[1] + split + " z: " + xyz[2];
        else
            return "nowhere";
    }

    /**
     * Saves the data object to file
     * 
     * @param name of the file prefix to use.
     * @return if the file save was successful
     */
    public boolean save(String fileName) {
        props.setProperty("invisible", Boolean.toString(invisible));
        props.setProperty("god", Boolean.toString(god));
        props.setProperty("admin-mode", Boolean.toString(adminMode));
        props.setProperty("stealthed", Boolean.toString(stealthed));
        props.setProperty("no-pickup", Boolean.toString(noPickup));
        if (world != null)
            props.setProperty("world", world);
        if (xyz != null) {
            props.setProperty("x", Integer.toString(xyz[0]));
            props.setProperty("y", Integer.toString(xyz[1]));
            props.setProperty("z", Integer.toString(xyz[2]));
        }
        try {
            props.store(new FileOutputStream(Administrate.playerDataPath + fileName + ".properties"), null);
            return true;
        } catch (IOException e){
            return false;
        }

    }

    /**
     * Attempts to load player data from a file
     * 
     * @param fileName
     * @return if the file was loaded into the player object properly.
     */
    public boolean load(String fileName) {
        File file = new File(Administrate.playerDataPath + fileName + ".properties");
        if (file.exists()) {
            try {
                props.load(new FileInputStream(Administrate.playerDataPath + fileName + ".properties"));
            } catch (IOException e){
                return false;
            }
        } else
            return false;

        //Load our values from the property mapping
        try {
            xyz = new int[] {Integer.valueOf(props.getProperty("x")), Integer.valueOf(props.getProperty("y")), Integer.valueOf(props.getProperty("z"))};
        } catch (NumberFormatException e) {
            xyz = null;
        }
        world = props.getProperty("world");
        god = Boolean.parseBoolean(props.getProperty("god", Boolean.toString(god)));
        invisible = Boolean.parseBoolean(props.getProperty("invisible", Boolean.toString(invisible)));
        adminMode = Boolean.parseBoolean(props.getProperty("admin-mode", Boolean.toString(adminMode)));
        stealthed = Boolean.parseBoolean(props.getProperty("stealthed", Boolean.toString(stealthed)));
        noPickup = Boolean.parseBoolean(props.getProperty("no-pickup", Boolean.toString(noPickup)));
        return true;
    }
}
