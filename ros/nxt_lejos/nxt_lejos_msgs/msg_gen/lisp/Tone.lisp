; Auto-generated. Do not edit!


(cl:in-package nxt_lejos_msgs-msg)


;//! \htmlinclude Tone.msg.html

(cl:defclass <Tone> (roslisp-msg-protocol:ros-message)
  ((pitch
    :reader pitch
    :initarg :pitch
    :type cl:fixnum
    :initform 0)
   (duration
    :reader duration
    :initarg :duration
    :type cl:fixnum
    :initform 0))
)

(cl:defclass Tone (<Tone>)
  ())

(cl:defmethod cl:initialize-instance :after ((m <Tone>) cl:&rest args)
  (cl:declare (cl:ignorable args))
  (cl:unless (cl:typep m 'Tone)
    (roslisp-msg-protocol:msg-deprecation-warning "using old message class name nxt_lejos_msgs-msg:<Tone> is deprecated: use nxt_lejos_msgs-msg:Tone instead.")))

(cl:ensure-generic-function 'pitch-val :lambda-list '(m))
(cl:defmethod pitch-val ((m <Tone>))
  (roslisp-msg-protocol:msg-deprecation-warning "Using old-style slot reader nxt_lejos_msgs-msg:pitch-val is deprecated.  Use nxt_lejos_msgs-msg:pitch instead.")
  (pitch m))

(cl:ensure-generic-function 'duration-val :lambda-list '(m))
(cl:defmethod duration-val ((m <Tone>))
  (roslisp-msg-protocol:msg-deprecation-warning "Using old-style slot reader nxt_lejos_msgs-msg:duration-val is deprecated.  Use nxt_lejos_msgs-msg:duration instead.")
  (duration m))
(cl:defmethod roslisp-msg-protocol:serialize ((msg <Tone>) ostream)
  "Serializes a message object of type '<Tone>"
  (cl:let* ((signed (cl:slot-value msg 'pitch)) (unsigned (cl:if (cl:< signed 0) (cl:+ signed 65536) signed)))
    (cl:write-byte (cl:ldb (cl:byte 8 0) unsigned) ostream)
    (cl:write-byte (cl:ldb (cl:byte 8 8) unsigned) ostream)
    )
  (cl:let* ((signed (cl:slot-value msg 'duration)) (unsigned (cl:if (cl:< signed 0) (cl:+ signed 65536) signed)))
    (cl:write-byte (cl:ldb (cl:byte 8 0) unsigned) ostream)
    (cl:write-byte (cl:ldb (cl:byte 8 8) unsigned) ostream)
    )
)
(cl:defmethod roslisp-msg-protocol:deserialize ((msg <Tone>) istream)
  "Deserializes a message object of type '<Tone>"
    (cl:let ((unsigned 0))
      (cl:setf (cl:ldb (cl:byte 8 0) unsigned) (cl:read-byte istream))
      (cl:setf (cl:ldb (cl:byte 8 8) unsigned) (cl:read-byte istream))
      (cl:setf (cl:slot-value msg 'pitch) (cl:if (cl:< unsigned 32768) unsigned (cl:- unsigned 65536))))
    (cl:let ((unsigned 0))
      (cl:setf (cl:ldb (cl:byte 8 0) unsigned) (cl:read-byte istream))
      (cl:setf (cl:ldb (cl:byte 8 8) unsigned) (cl:read-byte istream))
      (cl:setf (cl:slot-value msg 'duration) (cl:if (cl:< unsigned 32768) unsigned (cl:- unsigned 65536))))
  msg
)
(cl:defmethod roslisp-msg-protocol:ros-datatype ((msg (cl:eql '<Tone>)))
  "Returns string type for a message object of type '<Tone>"
  "nxt_lejos_msgs/Tone")
(cl:defmethod roslisp-msg-protocol:ros-datatype ((msg (cl:eql 'Tone)))
  "Returns string type for a message object of type 'Tone"
  "nxt_lejos_msgs/Tone")
(cl:defmethod roslisp-msg-protocol:md5sum ((type (cl:eql '<Tone>)))
  "Returns md5sum for a message object of type '<Tone>"
  "e1d9b86aeb1932bcd48bbf7f748a6c8d")
(cl:defmethod roslisp-msg-protocol:md5sum ((type (cl:eql 'Tone)))
  "Returns md5sum for a message object of type 'Tone"
  "e1d9b86aeb1932bcd48bbf7f748a6c8d")
(cl:defmethod roslisp-msg-protocol:message-definition ((type (cl:eql '<Tone>)))
  "Returns full string definition for message of type '<Tone>"
  (cl:format cl:nil "int16 pitch~%int16 duration~%~%~%"))
(cl:defmethod roslisp-msg-protocol:message-definition ((type (cl:eql 'Tone)))
  "Returns full string definition for message of type 'Tone"
  (cl:format cl:nil "int16 pitch~%int16 duration~%~%~%"))
(cl:defmethod roslisp-msg-protocol:serialization-length ((msg <Tone>))
  (cl:+ 0
     2
     2
))
(cl:defmethod roslisp-msg-protocol:ros-message-to-list ((msg <Tone>))
  "Converts a ROS message object to a list"
  (cl:list 'Tone
    (cl:cons ':pitch (pitch msg))
    (cl:cons ':duration (duration msg))
))
