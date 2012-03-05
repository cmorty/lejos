; Auto-generated. Do not edit!


(cl:in-package nxt_lejos_msgs-msg)


;//! \htmlinclude JointVelocity.msg.html

(cl:defclass <JointVelocity> (roslisp-msg-protocol:ros-message)
  ((name
    :reader name
    :initarg :name
    :type cl:string
    :initform "")
   (velocity
    :reader velocity
    :initarg :velocity
    :type cl:float
    :initform 0.0))
)

(cl:defclass JointVelocity (<JointVelocity>)
  ())

(cl:defmethod cl:initialize-instance :after ((m <JointVelocity>) cl:&rest args)
  (cl:declare (cl:ignorable args))
  (cl:unless (cl:typep m 'JointVelocity)
    (roslisp-msg-protocol:msg-deprecation-warning "using old message class name nxt_lejos_msgs-msg:<JointVelocity> is deprecated: use nxt_lejos_msgs-msg:JointVelocity instead.")))

(cl:ensure-generic-function 'name-val :lambda-list '(m))
(cl:defmethod name-val ((m <JointVelocity>))
  (roslisp-msg-protocol:msg-deprecation-warning "Using old-style slot reader nxt_lejos_msgs-msg:name-val is deprecated.  Use nxt_lejos_msgs-msg:name instead.")
  (name m))

(cl:ensure-generic-function 'velocity-val :lambda-list '(m))
(cl:defmethod velocity-val ((m <JointVelocity>))
  (roslisp-msg-protocol:msg-deprecation-warning "Using old-style slot reader nxt_lejos_msgs-msg:velocity-val is deprecated.  Use nxt_lejos_msgs-msg:velocity instead.")
  (velocity m))
(cl:defmethod roslisp-msg-protocol:serialize ((msg <JointVelocity>) ostream)
  "Serializes a message object of type '<JointVelocity>"
  (cl:let ((__ros_str_len (cl:length (cl:slot-value msg 'name))))
    (cl:write-byte (cl:ldb (cl:byte 8 0) __ros_str_len) ostream)
    (cl:write-byte (cl:ldb (cl:byte 8 8) __ros_str_len) ostream)
    (cl:write-byte (cl:ldb (cl:byte 8 16) __ros_str_len) ostream)
    (cl:write-byte (cl:ldb (cl:byte 8 24) __ros_str_len) ostream))
  (cl:map cl:nil #'(cl:lambda (c) (cl:write-byte (cl:char-code c) ostream)) (cl:slot-value msg 'name))
  (cl:let ((bits (roslisp-utils:encode-double-float-bits (cl:slot-value msg 'velocity))))
    (cl:write-byte (cl:ldb (cl:byte 8 0) bits) ostream)
    (cl:write-byte (cl:ldb (cl:byte 8 8) bits) ostream)
    (cl:write-byte (cl:ldb (cl:byte 8 16) bits) ostream)
    (cl:write-byte (cl:ldb (cl:byte 8 24) bits) ostream)
    (cl:write-byte (cl:ldb (cl:byte 8 32) bits) ostream)
    (cl:write-byte (cl:ldb (cl:byte 8 40) bits) ostream)
    (cl:write-byte (cl:ldb (cl:byte 8 48) bits) ostream)
    (cl:write-byte (cl:ldb (cl:byte 8 56) bits) ostream))
)
(cl:defmethod roslisp-msg-protocol:deserialize ((msg <JointVelocity>) istream)
  "Deserializes a message object of type '<JointVelocity>"
    (cl:let ((__ros_str_len 0))
      (cl:setf (cl:ldb (cl:byte 8 0) __ros_str_len) (cl:read-byte istream))
      (cl:setf (cl:ldb (cl:byte 8 8) __ros_str_len) (cl:read-byte istream))
      (cl:setf (cl:ldb (cl:byte 8 16) __ros_str_len) (cl:read-byte istream))
      (cl:setf (cl:ldb (cl:byte 8 24) __ros_str_len) (cl:read-byte istream))
      (cl:setf (cl:slot-value msg 'name) (cl:make-string __ros_str_len))
      (cl:dotimes (__ros_str_idx __ros_str_len msg)
        (cl:setf (cl:char (cl:slot-value msg 'name) __ros_str_idx) (cl:code-char (cl:read-byte istream)))))
    (cl:let ((bits 0))
      (cl:setf (cl:ldb (cl:byte 8 0) bits) (cl:read-byte istream))
      (cl:setf (cl:ldb (cl:byte 8 8) bits) (cl:read-byte istream))
      (cl:setf (cl:ldb (cl:byte 8 16) bits) (cl:read-byte istream))
      (cl:setf (cl:ldb (cl:byte 8 24) bits) (cl:read-byte istream))
      (cl:setf (cl:ldb (cl:byte 8 32) bits) (cl:read-byte istream))
      (cl:setf (cl:ldb (cl:byte 8 40) bits) (cl:read-byte istream))
      (cl:setf (cl:ldb (cl:byte 8 48) bits) (cl:read-byte istream))
      (cl:setf (cl:ldb (cl:byte 8 56) bits) (cl:read-byte istream))
    (cl:setf (cl:slot-value msg 'velocity) (roslisp-utils:decode-double-float-bits bits)))
  msg
)
(cl:defmethod roslisp-msg-protocol:ros-datatype ((msg (cl:eql '<JointVelocity>)))
  "Returns string type for a message object of type '<JointVelocity>"
  "nxt_lejos_msgs/JointVelocity")
(cl:defmethod roslisp-msg-protocol:ros-datatype ((msg (cl:eql 'JointVelocity)))
  "Returns string type for a message object of type 'JointVelocity"
  "nxt_lejos_msgs/JointVelocity")
(cl:defmethod roslisp-msg-protocol:md5sum ((type (cl:eql '<JointVelocity>)))
  "Returns md5sum for a message object of type '<JointVelocity>"
  "2fd9f71288435c0d2a1acae968c9052e")
(cl:defmethod roslisp-msg-protocol:md5sum ((type (cl:eql 'JointVelocity)))
  "Returns md5sum for a message object of type 'JointVelocity"
  "2fd9f71288435c0d2a1acae968c9052e")
(cl:defmethod roslisp-msg-protocol:message-definition ((type (cl:eql '<JointVelocity>)))
  "Returns full string definition for message of type '<JointVelocity>"
  (cl:format cl:nil "string name~%float64 velocity~%~%~%"))
(cl:defmethod roslisp-msg-protocol:message-definition ((type (cl:eql 'JointVelocity)))
  "Returns full string definition for message of type 'JointVelocity"
  (cl:format cl:nil "string name~%float64 velocity~%~%~%"))
(cl:defmethod roslisp-msg-protocol:serialization-length ((msg <JointVelocity>))
  (cl:+ 0
     4 (cl:length (cl:slot-value msg 'name))
     8
))
(cl:defmethod roslisp-msg-protocol:ros-message-to-list ((msg <JointVelocity>))
  "Converts a ROS message object to a list"
  (cl:list 'JointVelocity
    (cl:cons ':name (name msg))
    (cl:cons ':velocity (velocity msg))
))
