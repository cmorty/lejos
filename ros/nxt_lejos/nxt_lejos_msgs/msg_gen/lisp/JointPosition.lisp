; Auto-generated. Do not edit!


(cl:in-package nxt_lejos_msgs-msg)


;//! \htmlinclude JointPosition.msg.html

(cl:defclass <JointPosition> (roslisp-msg-protocol:ros-message)
  ((name
    :reader name
    :initarg :name
    :type cl:string
    :initform "")
   (angle
    :reader angle
    :initarg :angle
    :type cl:float
    :initform 0.0))
)

(cl:defclass JointPosition (<JointPosition>)
  ())

(cl:defmethod cl:initialize-instance :after ((m <JointPosition>) cl:&rest args)
  (cl:declare (cl:ignorable args))
  (cl:unless (cl:typep m 'JointPosition)
    (roslisp-msg-protocol:msg-deprecation-warning "using old message class name nxt_lejos_msgs-msg:<JointPosition> is deprecated: use nxt_lejos_msgs-msg:JointPosition instead.")))

(cl:ensure-generic-function 'name-val :lambda-list '(m))
(cl:defmethod name-val ((m <JointPosition>))
  (roslisp-msg-protocol:msg-deprecation-warning "Using old-style slot reader nxt_lejos_msgs-msg:name-val is deprecated.  Use nxt_lejos_msgs-msg:name instead.")
  (name m))

(cl:ensure-generic-function 'angle-val :lambda-list '(m))
(cl:defmethod angle-val ((m <JointPosition>))
  (roslisp-msg-protocol:msg-deprecation-warning "Using old-style slot reader nxt_lejos_msgs-msg:angle-val is deprecated.  Use nxt_lejos_msgs-msg:angle instead.")
  (angle m))
(cl:defmethod roslisp-msg-protocol:serialize ((msg <JointPosition>) ostream)
  "Serializes a message object of type '<JointPosition>"
  (cl:let ((__ros_str_len (cl:length (cl:slot-value msg 'name))))
    (cl:write-byte (cl:ldb (cl:byte 8 0) __ros_str_len) ostream)
    (cl:write-byte (cl:ldb (cl:byte 8 8) __ros_str_len) ostream)
    (cl:write-byte (cl:ldb (cl:byte 8 16) __ros_str_len) ostream)
    (cl:write-byte (cl:ldb (cl:byte 8 24) __ros_str_len) ostream))
  (cl:map cl:nil #'(cl:lambda (c) (cl:write-byte (cl:char-code c) ostream)) (cl:slot-value msg 'name))
  (cl:let ((bits (roslisp-utils:encode-double-float-bits (cl:slot-value msg 'angle))))
    (cl:write-byte (cl:ldb (cl:byte 8 0) bits) ostream)
    (cl:write-byte (cl:ldb (cl:byte 8 8) bits) ostream)
    (cl:write-byte (cl:ldb (cl:byte 8 16) bits) ostream)
    (cl:write-byte (cl:ldb (cl:byte 8 24) bits) ostream)
    (cl:write-byte (cl:ldb (cl:byte 8 32) bits) ostream)
    (cl:write-byte (cl:ldb (cl:byte 8 40) bits) ostream)
    (cl:write-byte (cl:ldb (cl:byte 8 48) bits) ostream)
    (cl:write-byte (cl:ldb (cl:byte 8 56) bits) ostream))
)
(cl:defmethod roslisp-msg-protocol:deserialize ((msg <JointPosition>) istream)
  "Deserializes a message object of type '<JointPosition>"
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
    (cl:setf (cl:slot-value msg 'angle) (roslisp-utils:decode-double-float-bits bits)))
  msg
)
(cl:defmethod roslisp-msg-protocol:ros-datatype ((msg (cl:eql '<JointPosition>)))
  "Returns string type for a message object of type '<JointPosition>"
  "nxt_lejos_msgs/JointPosition")
(cl:defmethod roslisp-msg-protocol:ros-datatype ((msg (cl:eql 'JointPosition)))
  "Returns string type for a message object of type 'JointPosition"
  "nxt_lejos_msgs/JointPosition")
(cl:defmethod roslisp-msg-protocol:md5sum ((type (cl:eql '<JointPosition>)))
  "Returns md5sum for a message object of type '<JointPosition>"
  "882c10e7eced8d306af293350cdb9414")
(cl:defmethod roslisp-msg-protocol:md5sum ((type (cl:eql 'JointPosition)))
  "Returns md5sum for a message object of type 'JointPosition"
  "882c10e7eced8d306af293350cdb9414")
(cl:defmethod roslisp-msg-protocol:message-definition ((type (cl:eql '<JointPosition>)))
  "Returns full string definition for message of type '<JointPosition>"
  (cl:format cl:nil "string name~%float64 angle~%~%~%"))
(cl:defmethod roslisp-msg-protocol:message-definition ((type (cl:eql 'JointPosition)))
  "Returns full string definition for message of type 'JointPosition"
  (cl:format cl:nil "string name~%float64 angle~%~%~%"))
(cl:defmethod roslisp-msg-protocol:serialization-length ((msg <JointPosition>))
  (cl:+ 0
     4 (cl:length (cl:slot-value msg 'name))
     8
))
(cl:defmethod roslisp-msg-protocol:ros-message-to-list ((msg <JointPosition>))
  "Converts a ROS message object to a list"
  (cl:list 'JointPosition
    (cl:cons ':name (name msg))
    (cl:cons ':angle (angle msg))
))
