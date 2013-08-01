/*
 * $RCSfile$
 *
 * Copyright 1997-2008 Sun Microsystems, Inc.  All Rights Reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Sun Microsystems, Inc., 4150 Network Circle, Santa Clara,
 * CA 95054 USA or visit www.sun.com if you need additional information or
 * have any questions.
 *
 * $Revision: 892 $
 * $Date: 2008-02-28 20:18:01 +0000 (Thu, 28 Feb 2008) $
 * $State$
 */

package javax.media.j3d;

import javax.vecmath.Quat4f;


/**
 * RotationPathInterpolator behavior.  This class defines a behavior
 * that varies the rotational component of its target TransformGroup
 * by linearly interpolating among a series of predefined knot/orientation
 * pairs (using the value generated by the specified Alpha object).  The
 * interpolated orientation is used to generate a rotation transform in
 * the local coordinate system.  The first knot must have a value of 0.0.
 * The last knot must have a value
 * of 1.0.  An intermediate knot with index k must have a value strictly
 * greater than any knot with index less than k.
 */

public class RotationPathInterpolator extends PathInterpolator {
    private Transform3D rotation = new Transform3D();

    private Quat4f tQuat = new Quat4f();

    // Array of quaternions at each knot
    private Quat4f quats[];
    private float prevInterpolationValue = Float.NaN;

    // We can't use a boolean flag since it is possible 
    // that after alpha change, this procedure only run
    // once at alpha.finish(). So the best way is to
    // detect alpha value change.
    private float prevAlphaValue = Float.NaN;
    private WakeupCriterion passiveWakeupCriterion = 
    (WakeupCriterion) new WakeupOnElapsedFrames(0, true);    
    // non-public, default constructor used by cloneNode
    RotationPathInterpolator() {
    }


    /**
     * Constructs a new RotationPathInterpolator object that varies the
     * target TransformGroup node's transform.
     * @param alpha the alpha object of this interpolator
     * @param target the TransformGroup node affected by this interpolator
     * @param axisOfTransform the transform that defines the local coordinate
     * system in which this interpolator operates
     * @param knots an array of knot values that specify interpolation points
     * @param quats an array of quaternion values at the knots
     * @exception IllegalArgumentException if the lengths of the
     * knots and quats arrays are not the same.
     */
    public RotationPathInterpolator(Alpha alpha,
				    TransformGroup target,
				    Transform3D axisOfTransform,
				    float[] knots,
				    Quat4f[] quats) {
	super(alpha,target, axisOfTransform, knots);

	if (knots.length != quats.length)
	    throw new IllegalArgumentException(J3dI18N.getString("RotationPathInterpolator0"));

	setPathArrays(quats);
    }


    /**
     * Sets the quat value at the specified index for this
     * interpolator.
     * @param index the index to be changed
     * @param quat the new quat value at the index
     */
    public void setQuat(int index, Quat4f quat) {
	this.quats[index].set(quat);
    }


    /**
     * Retrieves the quat value at the specified index.
     * @param index the index of the value requested
     * @param quat the quat object that will have the
     * quat value at index copied into it.
     */
    public void getQuat(int index, Quat4f quat) {
	quat.set(this.quats[index]);
    }


    /**
     * Replaces the existing arrays of knot values and quaternion
     * values with the specified arrays.
     * The arrays of knots and quats are copied
     * into this interpolator object.
     * @param knots a new array of knot values that specify
     * interpolation points
     * @param quats a new array of quaternion values at the knots
     * @exception IllegalArgumentException if the lengths of the
     * knots and quats arrays are not the same.
     *
     * @since Java 3D 1.2
     */
    public void setPathArrays(float[] knots,
			      Quat4f[] quats) {
	if (knots.length != quats.length)
	    throw new IllegalArgumentException(J3dI18N.getString("RotationPathInterpolator0"));

	setKnots(knots);
	setPathArrays(quats);
    }


    // Set the specific arrays for this path interpolator
    private void setPathArrays(Quat4f[] quats) {
	this.quats = new Quat4f[quats.length];
	for(int i = 0; i < quats.length; i++) {
	    this.quats[i] = new Quat4f();
	    this.quats[i].set(quats[i]);
	}
    }


    /**
     * Copies the array of quaternion values from this interpolator
     * into the specified array.
     * The array must be large enough to hold all of the quats.
     * The individual array elements must be allocated by the caller.
     * @param quats array that will receive the quats
     *
     * @since Java 3D 1.2
     */
    public void getQuats(Quat4f[] quats) {
	for (int i = 0; i < this.quats.length; i++)  {
	    quats[i].set(this.quats[i]);
	}
    }

    /**
     * @deprecated As of Java 3D version 1.3, replaced by
     * <code>TransformInterpolator.seTransformAxis(Transform3D)</code>
     */
    public void setAxisOfRotation(Transform3D axisOfRotation) {
	setTransformAxis(axisOfRotation);
    }
       
    /**
     * @deprecated As of Java 3D version 1.3, replaced by
     * <code>TransformInterpolator.getTransformAxis()</code>
     */
    public Transform3D getAxisOfRotation() {
        return getTransformAxis();
    }
    
    // The RotationPathInterpolator's initialize routine uses the default
    // initialization routine.


    /**
     * Computes the new transform for this interpolator for a given
     * alpha value.
     *
     * @param alphaValue alpha value between 0.0 and 1.0
     * @param transform object that receives the computed transform for
     * the specified alpha value
     *
     * @since Java 3D 1.3
     */
    public void computeTransform(float alphaValue, Transform3D transform) {
	float tt;
	double quatDot;
	computePathInterpolation(alphaValue);
	// For RPATH, take quaternion average and set rotation in TransformGroup
		
	if (currentKnotIndex == 0 &&
	    currentInterpolationValue == 0f) {
	    tQuat.x = quats[0].x;
	    tQuat.y = quats[0].y;
	    tQuat.z = quats[0].z;
	    tQuat.w = quats[0].w;
	} else {
	    quatDot = quats[currentKnotIndex].x *
		quats[currentKnotIndex+1].x +
		quats[currentKnotIndex].y *
		quats[currentKnotIndex+1].y +
		quats[currentKnotIndex].z *
		quats[currentKnotIndex+1].z +
		quats[currentKnotIndex].w *
		quats[currentKnotIndex+1].w;
	    if (quatDot < 0) {
		tQuat.x = quats[currentKnotIndex].x +
		    (-quats[currentKnotIndex+1].x -
		     quats[currentKnotIndex].x)*currentInterpolationValue;
		tQuat.y = quats[currentKnotIndex].y +
		    (-quats[currentKnotIndex+1].y -
		     quats[currentKnotIndex].y)*currentInterpolationValue;
		tQuat.z = quats[currentKnotIndex].z +
		    (-quats[currentKnotIndex+1].z -
		     quats[currentKnotIndex].z)*currentInterpolationValue;
		tQuat.w = quats[currentKnotIndex].w +
		    (-quats[currentKnotIndex+1].w -
		     quats[currentKnotIndex].w)*currentInterpolationValue;
	    } else {
		tQuat.x = quats[currentKnotIndex].x +
		    (quats[currentKnotIndex+1].x -
		     quats[currentKnotIndex].x)*currentInterpolationValue;
		tQuat.y = quats[currentKnotIndex].y +
		    (quats[currentKnotIndex+1].y -
		     quats[currentKnotIndex].y)*currentInterpolationValue;
		tQuat.z = quats[currentKnotIndex].z +
		    (quats[currentKnotIndex+1].z -
		     quats[currentKnotIndex].z)*currentInterpolationValue;
		tQuat.w = quats[currentKnotIndex].w +
		    (quats[currentKnotIndex+1].w -
		     quats[currentKnotIndex].w)*currentInterpolationValue;
	    }       
	}
		
	tQuat.normalize();	    
	rotation.set(tQuat);
		
	// construct a Transform3D from:  axis * rotation * axisInverse
	transform.mul(axis, rotation);
	transform.mul(transform, axisInverse);
    }

    /**
     * Used to create a new instance of the node.  This routine is called
     * by <code>cloneTree</code> to duplicate the current node.
     * @param forceDuplicate when set to <code>true</code>, causes the
     *  <code>duplicateOnCloneTree</code> flag to be ignored.  When
     *  <code>false</code>, the value of each node's
     *  <code>duplicateOnCloneTree</code> variable determines whether
     *  NodeComponent data is duplicated or copied.
     *
     * @see Node#cloneTree
     * @see Node#cloneNode
     * @see Node#duplicateNode
     * @see NodeComponent#setDuplicateOnCloneTree
     */
    public Node cloneNode(boolean forceDuplicate) {
        RotationPathInterpolator rpi = new RotationPathInterpolator();
        rpi.duplicateNode(this, forceDuplicate);
        return rpi;
    }


   /**
     * Copies all RotationPathInterpolator information from
     * <code>originalNode</code> into
     * the current node.  This method is called from the
     * <code>cloneNode</code> method which is, in turn, called by the
     * <code>cloneTree</code> method.<P> 
     *
     * @param originalNode the original node to duplicate.
     * @param forceDuplicate when set to <code>true</code>, causes the
     *  <code>duplicateOnCloneTree</code> flag to be ignored.  When
     *  <code>false</code>, the value of each node's
     *  <code>duplicateOnCloneTree</code> variable determines whether
     *  NodeComponent data is duplicated or copied.
     *
     * @exception RestrictedAccessException if this object is part of a live
     *  or compiled scenegraph.
     *
     * @see Node#duplicateNode
     * @see Node#cloneTree
     * @see NodeComponent#setDuplicateOnCloneTree
     */
    void duplicateAttributes(Node originalNode, boolean forceDuplicate) {
        super.duplicateAttributes(originalNode, forceDuplicate);

	RotationPathInterpolator ri = 
	    (RotationPathInterpolator) originalNode;

	int len = ri.getArrayLengths();

	// No API available to change size of array, so set here explicitly
        quats = new Quat4f[len];
        Quat4f quat = new Quat4f();

        for (int i = 0; i < len; i++) {
           quats[i] = new Quat4f();
           ri.getQuat(i, quat);
           setQuat(i, quat);
        }

    }
}
