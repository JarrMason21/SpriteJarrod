/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cst8218.maso0173.entity;

import java.awt.Color;
import java.awt.Graphics;
import java.io.Serializable;
import java.util.Random;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author tgk
 */

/*
The entity class for this JEE application. The AbstractFacade takes this entity 
class and sets it as the entity to be managed by the entity manager. Therefore 
the database will contain entities that are Sprites. This class defines what a 
sprites attributs and methods are.
*/
@Entity
@XmlRootElement
public class Sprite implements Serializable {

    private static final long serialVersionUID = 1L;

    public final static Random random = new Random();

    final static int SIZE = 10;
    final static int MAX_SPEED = 5;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    @Column
    //int wrapper class
    private Integer panelWidth = null;
    @Column
    //int wrapper class
    private Integer panelHeight = null;
    @Column
    //int wrapper class
    private Integer x = null;
    @Column
    //int wrapper class
    private Integer y = null;
    @Column
    //private int wrapper class
    private Integer dx = null;
    @Column
    //int wrapper class
    private Integer dy = null;
    @Column
    private Color color = Color.BLUE;

    public Sprite() {
    }

    public Sprite(int height, int width) {
        this.panelWidth = width;
        this.panelHeight = height;
        x = random.nextInt(width);
        y = random.nextInt(height);
        dx = random.nextInt(2 * MAX_SPEED) - MAX_SPEED;
        dy = random.nextInt(2 * MAX_SPEED) - MAX_SPEED;
    }

    public Sprite(int height, int width, Color color) {
        this(height, width);
        this.color = color;
    }

    /*To prevent null pointer exception: If null, make it 0*/
    public int getPanelWidth() {
        if(panelWidth == null)
            panelWidth = 0;
        return panelWidth;
    }

    public void setPanelWidth(int panelWidth) {
        this.panelWidth = panelWidth;
    }

    /*To prevent null pointer exception: If null, make it 0*/
    public int getPanelHeight() {
        if(panelHeight == null)
            panelHeight = 0;
        return panelHeight;
    }

    public void setPanelHeight(int panelHeight) {
        this.panelHeight = panelHeight;
    }

    /*To prevent null pointer exception: If null, make it 0*/
    public int getX() {
        if(x == null)
            x = 0;
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    /*To prevent null pointer exception: If null, make it 0*/
    public int getY() {
        if(y == null)
            y = 0;
        return y;
    }

    
    public void setY(int y) {
        this.y = y;
    }

    /*To prevent null pointer exception: If null, make it 0*/
    public int getDx() {
        if(dx == null)
            dx = 0;
        return dx;
    }

    public void setDx(int dx) {
        this.dx = dx;
    }

    /*To prevent null pointer exception: If null, make it 0*/
    public int getDy() {
        if(dy == null)
            dy = 0;
        return dy;
    }

    public void setDy(int dy) {
        this.dy = dy;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void draw(Graphics g) {
        g.setColor(color);
        g.fillOval(x, y, SIZE, SIZE);
    }

    public void move() {

        // check for bounce and make the ball bounce if necessary
        //
        if (x < 0 && dx < 0) {
            //bounce off the left wall 
            x = 0;
            dx = -dx;
        }
        if (y < 0 && dy < 0) {
            //bounce off the top wall
            y = 0;
            dy = -dy;
        }
        if (x > panelWidth - SIZE && dx > 0) {
            //bounce off the right wall
            x = panelWidth - SIZE;
            dx = -dx;
        }
        if (y > panelHeight - SIZE && dy > 0) {
            //bounce off the bottom wall
            y = panelHeight - SIZE;
            dy = -dy;
        }

        //make the ball move
        x += dx;
        y += dy;
    }
    
    /*
    Takes as a parameter the old sprite that will be updated by this method and
    updates it with the new sprites(this sprite) non-null attributes.
    */
    public Sprite update(Sprite oldSprite){
        if(x != null)
            oldSprite.setX(x);
        if(y != null)
            oldSprite.setY(y);
        if(dx != null)
            oldSprite.setDx(dx);
        if(dy != null)
            oldSprite.setDy(dy);
        if(panelHeight != null)
            oldSprite.setPanelHeight(panelHeight);
        if(panelWidth != null)
            oldSprite.setPanelWidth(panelWidth);
        
        return this;
    }
    
    /*
    Ensures that all the attributes are included in the body of the request so
    none of the attributes are null.
    */

    public boolean verifyData(){
        
        if(x == null)
            return false;
        if(y == null)
            return false;
        if(dx == null)
            return false;
        if(dy == null)
            return false;
        if(panelHeight == null)
            return false;
        if(panelWidth == null)
            return false;
        
        return true;
    }
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Sprite)) {
            return false;
        }
        Sprite other = (Sprite) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.Sprite[ id=" + id + " ]";
    }

}
