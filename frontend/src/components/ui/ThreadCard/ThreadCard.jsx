import React, { useState, useEffect, forwardRef } from 'react';
import { motion } from 'framer-motion';
import { ArrowBigUp, ArrowBigDown, MessageCircle } from 'lucide-react';
import { useNavigate } from 'react-router-dom';
import CountUp from '../../ui/CountUp/CountUp';
import { threadService } from '../../../services/threadService';

const ThreadCard = forwardRef(({ thread, showActions = true, layoutId, onVoteSuccess }, ref) => {
    const navigate = useNavigate();

    const getInitialVote = () => {
        if (thread.userVote === 'UPVOTE') return 1;
        if (thread.userVote === 'DOWNVOTE') return -1;
        return 0;
    };

    const [userVote, setUserVote] = useState(getInitialVote());
    const [voteCount, setVoteCount] = useState(thread.votes || thread.cfu || 0);
    const [isVoting, setIsVoting] = useState(false);

    useEffect(() => {
        setUserVote(getInitialVote());
        setVoteCount(thread.votes || thread.cfu || 0);
    }, [thread.id, thread.userVote]);

    const getTopicColor = (topic) => {
        const normalizedTopic = topic?.toUpperCase() || "";

        const colors = {
            "GENERALE": "text-teal-300 bg-teal-500/20 border-teal-500/30",
            "APPUNTI": "text-amber-300 bg-amber-500/20 border-amber-500/30",
            "ESAMI": "text-rose-300 bg-rose-500/20 border-rose-500/30",
            "CERCO_CASA": "text-orange-300 bg-orange-500/20 border-orange-500/30",
            "CERCASI_COINQUILINO": "text-orange-300 bg-orange-500/20 border-orange-500/30",
            "INFO": "text-blue-300 bg-blue-500/20 border-blue-500/30",
            "GRUPPO_STUDIO": "text-purple-300 bg-purple-500/20 border-purple-500/30",
            "SESSIONE": "text-red-300 bg-red-500/20 border-red-500/30",
            "VITA_FUORISEDE": "text-purple-300 bg-purple-500/20 border-purple-500/30",
            "APERITIVO": "text-pink-300 bg-pink-500/20 border-pink-500/30",
            "NOTTE_ESAME": "text-indigo-300 bg-indigo-500/20 border-indigo-500/30",
        };

        return colors[normalizedTopic] || colors[topic] || "text-slate-300 bg-slate-500/20 border-slate-500/30";
    };

    const getUniColor = (uni) => {
        if (uni?.includes("Politecnico")) return "text-blue-300 bg-blue-500/20 border-blue-500/30";
        if (uni?.includes("Sapienza") || uni?.includes("Roma")) return "text-red-300 bg-red-800/20 border-red-500/30";
        if (uni?.includes("Bocconi") || uni?.includes("Milano")) return "text-indigo-300 bg-indigo-500/20 border-indigo-500/30";
        if (uni?.includes("Federico II") || uni?.includes("Napoli")) return "text-yellow-200 bg-yellow-600/20 border-yellow-500/30";
        return "text-slate-300 bg-slate-500/20 border-slate-500/30";
    };

    const handleVoteApi = async (voteType) => {
        if (isVoting) return false;

        setIsVoting(true);
        try {
            const res = await threadService.voteThread(thread.id, voteType);

            if (res.data?.internalCode === 'REACT_SUCCESSFULLY') {
                return true;
            }
            return false;
        } catch (error) {
            return false;
        } finally {
            setIsVoting(false);
        }
    };

    const handleUpvote = async () => {
        if (isVoting) return;

        const previousVote = userVote;
        const previousCount = voteCount;

        if (userVote === 1) {
            setUserVote(0);
            setVoteCount(prev => prev - 1);

            const success = await handleVoteApi('UPVOTE');

            if (!success) {
                setUserVote(previousVote);
                setVoteCount(previousCount);
            }
        } else {
            const increment = userVote === -1 ? 2 : 1;
            setUserVote(1);
            setVoteCount(prev => prev + increment);

            const success = await handleVoteApi('UPVOTE');

            if (!success) {
                setUserVote(previousVote);
                setVoteCount(previousCount);
            }
        }
    };

    const handleDownvote = async () => {
        if (isVoting) return;

        const previousVote = userVote;
        const previousCount = voteCount;

        if (userVote === -1) {
            setUserVote(0);
            setVoteCount(prev => prev + 1);

            const success = await handleVoteApi('DOWNVOTE');

            if (!success) {
                setUserVote(previousVote);
                setVoteCount(previousCount);
            }
        } else {
            const decrement = userVote === 1 ? 2 : 1;
            setUserVote(-1);
            setVoteCount(prev => prev - decrement);

            const success = await handleVoteApi('DOWNVOTE');

            if (!success) {
                setUserVote(previousVote);
                setVoteCount(previousCount);
            }
        }
    };


    const getVoteColorClass = () => {
        if (userVote === 1) return 'text-[#77DD77]';
        if (userVote === -1) return 'text-[#FF6961]';
        return 'text-white';
    };

    const getVoteContainerStyles = () => {
        if (userVote === 1) return "bg-[#77DD77]/20 border-[#77DD77]/50 shadow-[0_0_15px_#77DD7740]";
        if (userVote === -1) return "bg-[#FF6961]/20 border-[#FF6961]/50 shadow-[0_0_15px_#FF696140]";
        return "bg-black/20 border-white/5";
    };

    const handleOpenDiscussion = () => {
        navigate(`/thread/${thread.id}`, { state: { thread: {...thread, votes: voteCount} } });
    };

    return (
        <motion.div
            layoutId={layoutId}
            layout
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.4, ease: "easeInOut" }}
            className="flex flex-col gap-4 p-6 bg-slate-900/40 backdrop-blur-xl border border-white/10 rounded-[2rem] hover:border-younify-blue/30 transition-colors shadow-lg w-full relative z-0"
        >
            <motion.div layout="position" className="flex flex-col gap-3">
                <div className="flex items-center justify-between text-xs text-slate-400">
                    <div className="flex items-center gap-2">
                        {thread.profileImage ? (
                            <img
                                src={thread.profileImage}
                                alt={thread.author}
                                className="w-8 h-8 rounded-full object-cover border-2 border-white/10 shadow-lg"
                                onError={(e) => {
                                    // Fallback se immagine non carica
                                    e.target.style.display = 'none';
                                    e.target.nextElementSibling.style.display = 'flex';
                                }}
                            />
                        ) : null}

                        {/* Fallback avatar con iniziale */}
                        <div
                            className="w-8 h-8 rounded-full bg-gradient-to-tr from-younify-blue to-purple-500 flex items-center justify-center text-white font-bold text-[10px] shadow-inner"
                            style={{ display: thread.profileImage ? 'none' : 'flex' }}
                        >
                            {thread.author?.charAt(0)?.toUpperCase() || "A"}
                        </div>

                        <span className="font-medium text-slate-200 text-sm">{thread.author}</span>
                    </div>
                    <span>{thread.timeAgo}</span>
                </div>
                <div className="flex items-center gap-2 flex-wrap">
                    <span className={`px-3 py-1 rounded-full text-[10px] font-bold border truncate max-w-[150px] ${getUniColor(thread.university)}`}>
                        {thread.university}
                    </span>
                    <span className={`px-3 py-1 rounded-full text-[10px] font-bold border ${getTopicColor(thread.topic)}`}>
                        {thread.topic}
                    </span>
                </div>
            </motion.div>

            <motion.div layout="position">
                <p className="text-slate-200 text-sm leading-relaxed font-light font-sans">{thread.content}</p>
            </motion.div>

            <motion.div layout="position" className="flex items-center justify-between mt-2 pt-4 border-t border-white/10">
                {showActions ? (
                    <button
                        onClick={handleOpenDiscussion}
                        className="px-4 py-2 rounded-xl bg-white text-slate-950 font-bold text-xs hover:bg-slate-200 transition-colors flex items-center gap-2 shadow-lg shadow-white/5"
                    >
                        <MessageCircle size={16} />
                        <span className="hidden sm:inline">Apri discussione</span>
                        <span className="sm:hidden">Apri</span>
                    </button>
                ) : (
                    <motion.span
                        initial={{ opacity: 0 }}
                        animate={{ opacity: 1 }}
                        transition={{ delay: 0.2 }}
                        className="text-slate-500 text-xs font-medium"
                    >
                        Discussione aperta
                    </motion.span>
                )}
                <div className={`flex items-center gap-2 px-3 py-1.5 rounded-xl border backdrop-blur-md transition-all duration-300 ${getVoteContainerStyles()}`}>
                    <button
                        onClick={handleDownvote}
                        disabled={isVoting}
                        className={`p-1 rounded-lg hover:bg-white/10 transition-colors disabled:opacity-50 ${userVote === -1 ? 'text-rose-500' : 'text-slate-400'}`}
                    >
                        <ArrowBigDown size={20} fill={userVote === -1 ? "currentColor" : "none"} />
                    </button>
                    <div className={`flex items-baseline gap-1 font-mono font-bold text-sm ${getVoteColorClass()}`}>
                        <CountUp from={0} to={voteCount} duration={0.5} />
                        <span className={`text-[10px] font-sans font-bold ${getVoteColorClass()}`}>CFU</span>
                    </div>
                    <button
                        onClick={handleUpvote}
                        disabled={isVoting}
                        className={`p-1 rounded-lg hover:bg-white/10 transition-colors disabled:opacity-50 ${userVote === 1 ? 'text-emerald-400' : 'text-slate-400'}`}
                    >
                        <ArrowBigUp size={20} fill={userVote === 1 ? "currentColor" : "none"} />
                    </button>
                </div>
            </motion.div>
        </motion.div>
    );
});

export default ThreadCard;
